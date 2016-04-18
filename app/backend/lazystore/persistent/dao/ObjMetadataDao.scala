package backend.lazystore.persistent.dao

import backend.lazystore.persistent.SlickDBPoolManager
import backend.lazystore.persistent.T.Tables.{Objmetadata, ObjmetadataRow}
import backend.lazystore.persistent.dao.SqlProvider._
import backend.base.util.{RestException, RestRespCode}

import scala.slick.driver.MySQLDriver.simple._

/**
 *  15-4-24.
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
        case ex:Exception=>throw new RestException(RestRespCode.db_executor_error,s"db executor error,${ex.getMessage}")
      }
    }
  }

}
//SlickDBPoolManager.DBPool.withTransaction { implicit session =>
//(Lazytopic returning Lazytopic.map(_.location)).insert(bean)
//}
