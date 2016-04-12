package cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.persistent.T.Tables.{ObjmetadataRow,Objmetadata}
import cn.changhong.lazystore.persistent.dao.SqlProvider._
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException}

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by yangguo on 15-4-24.
 */
object ObjMetadataDao {
  //system_path,creator,creation,checksum
  def add(map:Map[String,String])={
    val (keys,values)=createKeyValues(map)
    val sql=s"insert into objmetadata(${keys}) values(${values})"
    exec(sql)
  }
  def add(objMetadata:ObjmetadataRow)={
    SlickDBPoolManager.DBPool.withTransaction{implicit session=>
      try {
        (Objmetadata returning Objmetadata.map(_.id)).insert(objMetadata)
      }catch{
        case ex:Exception=>throw new RestException(RestResponseInlineCode.db_executor_error,s"db executor error,${ex.getMessage}")
      }
    }
  }

}
//SlickDBPoolManager.DBPool.withTransaction { implicit session =>
//(Lazytopic returning Lazytopic.map(_.location)).insert(bean)
//}
