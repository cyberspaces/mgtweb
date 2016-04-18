package backend.lazystore.persistent.dao

import backend.lazystore.persistent.SlickDBPoolManager
import backend.lazystore.persistent.T.Tables.ApptagsRow
import backend.lazystore.persistent.T.Tables.Apptags
import backend.base.util.{RestException, RestRespCode}

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
