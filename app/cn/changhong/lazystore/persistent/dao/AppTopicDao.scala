package cn.changhong.lazystore.persistent.dao

import java.util.Date
import cn.changhong.lazystore.service.AppsRequest
import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.util.{RestResponseInlineCode, RestException, RestRequest, Parser}

import cn.changhong.lazystore.persistent.T.Tables._

import scala.slick.driver.MySQLDriver.simple._

import scala.slick.jdbc.StaticQuery.interpolation

import SqlProvider._
/**
 *  15-1-23.
 */
object AppTopicDao {
  private[this] val c_lazytopic_position="topicposition"
  private[this] val T_LAZYTOPIC="lazytopic"
  private[this] val c_lazytopic_location="location"
  def addAppTopic(request:RestRequest)={
    val bean = Parser.LazyTopicParser(Parser.ChannelBufferToString(request.underlying.getContent))
    bean.creation = new Date().getTime
    try {
      SlickDBPoolManager.DBPool.withTransaction { implicit session =>
        (Lazytopic returning Lazytopic.map(_.location)).insert(bean)
      }
    }catch{
      case ex:Exception=>throw new RestException(RestResponseInlineCode.db_executor_error,s"db executor error,${ex.getMessage}")
    }
  }

  def getAppTopics(request:AppsRequest)={
    val where=request.condition match{
      case Some(s)=>s"$c_lazytopic_position='$s'"
      case None=>"1=1"
    }
    val columns=request.columns match{
      case Some(s)=>s
      case None=>"*"
    }
    val sql=s"select $columns from $T_LAZYTOPIC where $where order by $c_lazytopic_location"
    exec(sql)
  }

  def getAllTopicPosition={
    val sql=s"select id,name from lazytopicposition";
    exec(sql)
  }

  def getAllTopicTemplate={
    val sql=s"select topictemplate_id,name,url,template from topictemplate";
    exec(sql)
  }
  @Deprecated
  def getLocationTopics_Old(where:String,columns:String,size:Int,noHistory:Boolean,sid:Long=Long.MaxValue)={
    val sql=if(noHistory){
      s"select creation as sid,$columns from lazytopic where $where and creation<$sid and expired >= ${System.currentTimeMillis()} order by creation desc limit $size"
    }else{
      s"select creation as sid,$columns from lazytopic where $where and creation<$sid order by creation desc limit $size"
    }
    exec(sql)
  }
  def getLocationTopics(where:String,columns:String,noHistory:Boolean,offset:Int,limit:Int) ={
    val sql=if(noHistory){
      s"select creation as sid,$columns from lazytopic where $where and expired >= ${System.currentTimeMillis()-24*60*60*1000} order by creation desc limit $offset,$limit";
    }else{
      s"select creation as sid,$columns from lazytopic where $where  order by creation desc limit $offset,$limit";
    }
    exec(sql)
  }
  def getCount(where:String,noHistory:Boolean) ={
    val sql=if(noHistory){
      s" select count(1) from lazytopic where $where and creation >= ${System.currentTimeMillis()}"
    }else{
      s" select count(1) from lazytopic where $where"
    }
    exec1(sql)
  }

  def addTopic(map:Map[String,String])={
    val columns=map.map(_._1).reduce(_+","+_)
    val values=map.map("'"+_._2+"'").reduce(_+","+_)
    val sql=s"insert into lazytopic($columns) values($values)"
    exec1(sql)
  }
  def addTopicPosition(map:Map[String,String])={
    val columns=map.map(_._1).reduce(_+","+_)
    val values=map.map("'"+_._2+"'").reduce(_+","+_)
    val sql=s"insert into lazytopicposition($columns) values($values)"
    exec1(sql)
  }

  def updateTopic(map:Map[String,String])={
    val kv=map.map{kv=>kv._1+"='"+kv._2+"'"}.reduce(_+","+_)
    val where="title='"+map.getOrElse("title","default_0")+"'"
    val sql=s"update lazytopic set ${kv} where ${where}"
    exec1(sql)
  }





}