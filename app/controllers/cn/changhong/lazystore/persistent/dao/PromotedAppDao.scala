package controllers.cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.persistent.dao.SqlProvider

/**
 * Created by yangguo on 15-4-29.
 */
object PromotedAppDao {
  def add(map:Map[String,String])={
    if(checkPromotedTimeValid(map)) {
      val keys = map.map(_._1).reduce(_ + "," + _)
      val values = map.map { kv =>
        "'" + kv._2 + "'"
      }.reduce(_ + "," + _)
      val sql = s"insert into prometedapps(${keys}) values(${values})"
      SqlProvider.exec1(sql)
    }else throw new RuntimeException("此推广位置在此推广时间段已经被占用")
  }
  private[this] def checkPromotedTimeValid(map:Map[String,String]):Boolean={
    if(map.getOrElse("status","default")=="发布") {
      val lauchtime=map.getOrElse("launchtime","default")
      val expired=map.getOrElse("expired","default")
      val where=map.getOrElse("mainposition","default") match{
        case "分类-推荐应用列表"=> s"mainposition='分类-推荐应用列表' and subposition='${map.getOrElse("subposition","default")}'"
        case item=>s"mainposition='$item'"
      }
      val sql=s"select count(1) from prometedapps where launchtime<=${lauchtime} and expired>=${expired} and $where and status='发布'"
      val count=SqlProvider.exec1(sql) match{
        case item::list=>item
        case i=> 0
      }
      if(count>0) false
      else true
    }else true
  }
  def update(newValues:Map[String,String],wheres:Map[String,String]) ={
    val up=newValues.map(kv=>kv._1+"='"+kv._2+"'").reduce(_+","+_)
    val where=wheres.map(kv=>kv._1+"='"+kv._2+"'").reduce(_+" and "+_)
    val sql=s"update prometedapps set ${up} where ${where}"
    SqlProvider.exec1(sql)
  }
  def select(columns:String,where:String)={
    val sql=s"select ${columns} from prometedapps where ${where}"
    SqlProvider.exec(sql)
  }
  def pagingSelect(columns:String,where:String,limit:Int,offset:Int) ={
    val sql=s"select ${columns} from prometedapps where ${where} order by creation desc limit ${offset},${limit}"
    SqlProvider.exec(sql)
  }
  def update(kvs:Map[String,String],where:String)={
    if(checkPromotedTimeValid(kvs)) {
      val values = kvs.map { kv => kv._1 + "='" + kv._2 + "'" }.reduce(_ + "," + _)
      val sql = s"update prometedapps set ${values} where $where"
      SqlProvider.exec1(sql)
    }else throw new RuntimeException("此推广位置在此推广时间段已经被占用")
  }
  def count(where:String)={
    val sql=s"select count(1) from prometedapps where ${where}"
    SqlProvider.exec1(sql) match {
      case item::list=>item
      case item=>0
    }
  }
}
