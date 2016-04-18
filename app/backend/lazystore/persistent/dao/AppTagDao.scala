package cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.persistent.SlickDBPoolManager
import cn.changhong.lazystore.persistent.T.Tables.ApptagsRow
import cn.changhong.lazystore.persistent.T.Tables.Apptags
import cn.changhong.base.util.{RestException, RestRespCode}

import scala.slick.driver.MySQLDriver.simple._

/**
 *  15-2-10.
 */
object AppTagDao {
  def insertAppTags(tags:Seq[ApptagsRow]) ={
    try {
      SlickDBPoolManager.DBPool.withSession { implicit session =>
        Apptags.insertAll(tags: _*)
      }
    }catch{
      case ex:Throwable=>throw new RestException(RestRespCode.db_executor_error,s"db executor error,${ex.getMessage}")
    }
  }
}
