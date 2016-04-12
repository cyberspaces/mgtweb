package cn.changhong.lazystore.persistent.dao

import java.util.Date

import cn.changhong.lazystore.util.KeyGenerator
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException, Parser, RestRequest}
import scala.slick.driver.MySQLDriver.simple._
import cn.changhong.lazystore.persistent.T.Tables._

/**
 * Created by yangguo on 15-1-23.
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
      case ex: Exception => throw new RestException(RestResponseInlineCode.db_executor_error, s"db executor error,${ex.getMessage}")
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
      case ex: Exception => throw new RestException(RestResponseInlineCode.db_executor_error, s"db executor error,${ex.getMessage}")

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
      case ex: Exception => throw new RestException(RestResponseInlineCode.db_executor_error, s"db executor error,${ex.getMessage}")
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
      case ex: Exception => throw new RestException(RestResponseInlineCode.db_executor_error, s"db executor error,${ex.getMessage}")
    }
  }


}
