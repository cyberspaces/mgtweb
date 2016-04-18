package cn.changhong.lazystore.persistent.dao

/**
 *  15-2-10.
 */

import cn.changhong.lazystore.persistent.SlickDBPoolManager
import cn.changhong.lazystore.persistent.T.Tables.{Appdev, AppdevRow}
import cn.changhong.base.util.{RestException, RestRespCode}

import scala.slick.driver.MySQLDriver.simple._
object AppDeveloperDao{
  def insertDeveloper(dev:AppdevRow)={
    try {
      SlickDBPoolManager.DBPool.withSession { implicit session =>
        Appdev.insert(dev)
      }
    }catch{
      case ex:Exception=>throw new RestException(RestRespCode.db_executor_error,s"db executor error,${ex.getMessage}")
    }
  }
}
