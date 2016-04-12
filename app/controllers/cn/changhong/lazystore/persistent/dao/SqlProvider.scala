package cn.changhong.lazystore.persistent.dao

import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException}

import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation

/**
 * Created by Administrator on 2015/2/2.
 */
object SqlProvider {
  def createKeyValues(map:Map[String,String])={
    val keys=map.map(_._1).reduce(_+","+_)
    val values=map.map("'"+_._2+"'").reduce(_+","+_)
    (keys,values)
  }
  def exec(sql:String)={
    try{
      println(sql)
      SlickDBPoolManager.DBPool.withSession{implicit session=>
        sql"#$sql".as(SlickResultMap).list
      }
    }catch{
      case ex => throw new RestException(RestResponseInlineCode.db_executor_error, s"${ex.getMessage}")
    }
  }
  def execTransaction(sqls:List[(Int,String)]) ={
    try{
      sqls.foreach(println)
      SlickDBPoolManager.DBPool.withTransaction{implicit session=>
        sqls.sortBy(kv=> kv._1).map{kv=>
          val sql=kv._2
          sql"#$sql".as(SlickResultMap).list
        }
      }
    }catch{
      case ex => throw new RestException(RestResponseInlineCode.db_executor_error, s"${ex.getMessage}")
    }

  }
  def exec1(sql:String)={
    try{
      println(sql)
      SlickDBPoolManager.DBPool.withSession{implicit session=>
        sql"#$sql".as(SlickResultInt).list
      }
    }catch{
      case ex => throw new RestException(RestResponseInlineCode.db_executor_error, s"${ex.getMessage}")
    }
  }
  def exec2(sql:String)={
    try{
      println(sql)
      SlickDBPoolManager.DBPool.withSession{implicit session=>
        sql"#$sql".as(SlickResultString).list
      }
    }catch{
      case ex => throw new RestException(RestResponseInlineCode.db_executor_error, s"${ex.getMessage}")
    }
  }
}
