package cn.changhong.lazystore.persistent.dao

/**
 * Created by yangguo on 15-2-10.
 */

import cn.changhong.lazystore.persistent.T.Tables.{AppdevRow,Appdev}
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException}

import scala.slick.driver.MySQLDriver.simple._
object AppDeveloperDao{
  def insertDeveloper(dev:AppdevRow)={
    try {
      SlickDBPoolManager.DBPool.withSession { implicit session =>
        Appdev.insert(dev)
      }
    }catch{
      case ex:Exception=>throw new RestException(RestResponseInlineCode.db_executor_error,s"db executor error,${ex.getMessage}")
    }
  }
}
