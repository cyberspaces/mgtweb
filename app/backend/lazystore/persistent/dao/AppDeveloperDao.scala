package backend.lazystore.persistent.dao

/**
 *  15-2-10.
 */

import backend.lazystore.persistent.SlickDBPoolManager
import backend.lazystore.persistent.T.Tables.{Appdev, AppdevRow}
import backend.base.util.{RestException, RestRespCode}

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
