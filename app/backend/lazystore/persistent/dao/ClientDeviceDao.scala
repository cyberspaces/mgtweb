package backend.lazystore.persistent.dao

import java.util.Date

import backend.lazystore.persistent.SlickDBPoolManager
import backend.lazystore.util.KeyGenerator
import backend.base.util.{Parser, RestException, RestRequest, RestRespCode}

import scala.slick.driver.MySQLDriver.simple._
import backend.lazystore.persistent.T.Tables._

/**
 *  15-1-23.
 */
case class DeviceAppsStat(deviceId:Long,stats:Array[UAppstatsRow],statsType:String)
case class DeviceApps(uDeviceId:Long,uApps:Array[UAppsRow])

object ClientDeviceDao {
  def addClientDevice(request: RestRequest) = {
    val device = Parser.UDeviceParser(Parser.ChannelBufferToString(request.underlying.getContent))
    device.registerdate = new Date().getTime
    device.id = -1
    device.uuid = KeyGenerator.createUUID
    device.isbind = 0
    try {
      SlickDBPoolManager.DBPool.withTransaction { implicit session =>
        (UDevice returning UDevice.map(_.id)).insert(device)
      }
    } catch {
      case ex: Exception => throw new RestException(RestRespCode.db_executor_error, s"db executor error,${ex.getMessage}")
    }
  }

  def addClientDeviceApps(uapps:Array[UAppsRow])={
    try{
      SlickDBPoolManager.DBPool.withTransaction{implicit session=>
        UApps.insertAll(uapps:_*)
      } match{
        case Some(count)=>count
        case _=> 0
      }
    }catch {
      case ex: Exception => throw new RestException(RestRespCode.db_executor_error, s"db executor error,${ex.getMessage}")

    }
  }
  def addClientDeviceCopStats(request: RestRequest) = {
    val deviceStats = Parser.DeviceAppsStatsParser(Parser.ChannelBufferToString(request.underlying.getContent))
    val date = new Date().getTime()
    val stats = deviceStats.stats.map { deviceStat =>
      deviceStat.statsdate = date
      deviceStat.deviceId = deviceStats.deviceId
      deviceStat
    }
    try {
      SlickDBPoolManager.DBPool.withTransaction { implicit session =>
        UAppstats.insertAll(stats: _*) //.insert(stats)
      } match {
        case Some(s) => s
        case _ => 0
      }
    } catch {
      case ex: Exception => throw new RestException(RestRespCode.db_executor_error, s"db executor error,${ex.getMessage}")
    }
  }


  def addClientDeviceCopStats(request: String) = {
    val deviceStats = Parser.DeviceAppsStatsParser(request) //Parser.ChannelBufferToString(request.underlying.getContent))
    val date = new Date().getTime()
    val stats = deviceStats.stats.map { deviceStat =>
      deviceStat.statsdate = date
      deviceStat.deviceId = deviceStats.deviceId
      deviceStat
    }
    try {
      val res = SlickDBPoolManager.DBPool.withTransaction { implicit session =>
        UAppstats.insertAll(stats: _*) //.insert(stats)
      }
      res match {
        case Some(s) => s
        case _ => 0
      }
    } catch {
      case ex: Exception => throw new RestException(RestRespCode.db_executor_error, s"db executor error,${ex.getMessage}")
    }
  }


}
