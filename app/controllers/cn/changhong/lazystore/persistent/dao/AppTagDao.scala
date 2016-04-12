package cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.persistent.T.Tables.ApptagsRow
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.lazystore.persistent.T.Tables.Apptags
import cn.changhong.web.util.{RestResponseInlineCode, RestException}
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by yangguo on 15-2-10.
 */
object AppTagDao {
  def insertAppTags(tags:Seq[ApptagsRow]) ={
    try {
      SlickDBPoolManager.DBPool.withSession { implicit session =>
        Apptags.insertAll(tags: _*)
      }
    }catch{
      case ex:Throwable=>throw new RestException(RestResponseInlineCode.db_executor_error,s"db executor error,${ex.getMessage}")
    }
  }
}
