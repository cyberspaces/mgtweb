package cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.persistent.T.Tables._
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException}

import scala.slick.driver.MySQLDriver.simple._


/**
 *  15-4-14.
 */

object AdminAcountDao {
  val role_admin="admin"
  val role_dev="developer"
  val role_operator="operator"
  val status_apply="apply"
  val status_release="release"
  val status_no_pass="no_pass"

  def insert(admin:LazyadminRow) ={
    SlickDBPoolManager.DBPool.withTransaction{implicit session=>
      try {
        (Lazyadmin returning Lazyadmin.map(_.id)).insert(admin)
      }catch{
        case ex:Exception=>throw new RestException(RestResponseInlineCode.db_executor_error,s"db executor error,${ex.getMessage}")
      }
    }
  }
  def selectAdmin(name:String,passwd:String)={
    val sql=s"select name,role,id,email from lazyadmin where name='${name}' and passwd='${passwd}'"
    SqlProvider.exec(sql)
  }
  def selectAll={
    val sql="select name,role,phone,idcard,email,status from lazyadmin"
    SqlProvider.exec(sql)
  }
  def addAdmin(kv:Map[String,String])={

    val map=kv+("creation"->System.currentTimeMillis())+("status"->"release")
    val keys=map.map(_._1).reduce(_+","+_)
    val values=map.map("'"+_._2+"'").reduce(_+","+_)
    val sql=s"insert into lazyadmin($keys) values($values)"

    println(sql)
    SqlProvider.exec1(sql)
  }
  def deleteAdmin(id:String)={
    val sql=s"delete from lazyadmin where id='$id'"
    SqlProvider.exec1(sql)
  }


}