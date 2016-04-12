package cn.changhong.lazystore.persistent.dao

import java.util.Date
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by yangguo on 15-4-23.
 */
object AddData {
  def main(args: Array[String]) {
//    val index=new AtomicInteger(1);
//    val sql="select * from lazytopic";
//    val maps=SqlProvider.exec(sql);
//    val sql1="delete from lazytopic";
//    val rs=SqlProvider.exec1(sql1);
//    maps.map{item=>item+("creation"->(item.getOrElse("creation",new Date().getTime).toString.toLong+index.getAndIncrement))}.foreach{item=>
//      val keys=item.map(_._1).reduce(_+","+_)
//      val values=item.map("'"+_._2+"'").reduce(_+","+_)
//      val inSql=s"insert into lazytopic($keys) values($values)"
//      println(inSql)
//      SqlProvider.exec1(inSql)
//    }
    val creation=System.currentTimeMillis()
    (1 to 100).foreach { index =>
      val topicTitle = "测试"
      val expired = 1
      val launchTime = 1
      val insertSQL = s"insert into lazytopic(" +
        s"title," +
        s"subtitle," +
        s"img," +
        s"topictype," +
        s"topicposition," +
        s"creation," +
        s"action, " +
        s"lazyadmin_id," +
        s"expired," +
        s"topicstatus," +
        s"location," +
        s"topictemplate_name," +
        s"lazytopicposition_name," +
        s"launchtime) " +
        s"VALUES(" +
        s"'${topicTitle+index}'," +
        s"'视频'," +
        s"'http://img.wdjimg.com/mms/icon/v1/f/6a/fdd03595b0c2315159c955d967ce86af_48_48.png'," +
        s"'图像'," +
        s"'首页top1'," +
        s"${creation +index*1000}," +
        s"'跳转转转'," +
        s"1," +
        s"${creation+index*10000}," +
        s"'活动'," +
        s"'1'," +
        s"'小清新专题模板1'," +
        s"'首页top2'," +
        s" ${creation+index*20000})"
      SqlProvider.exec1(insertSQL)
    }

  }
}
