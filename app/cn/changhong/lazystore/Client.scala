package cn.changhong.lazystore

import java.net.URLEncoder
import java.util.UUID

import cn.changhong.lazystore.persistent.dao.AppTopicDao
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}

/**
 *  15-2-3.
 */
object Client {
  def main(args:Array[String]): Unit ={
    val res=List("首页top1","首页top2","首页top3","首页top4","首页top5","首页middle1","首页middle2","首页middle3").map{name=>
      val map:Map[String,String]=Map("name"->name,"lazyadmin_id"->"1423548742269","description"->"系统添加","creation"->System.currentTimeMillis().toString)
      AppTopicDao.addTopicPosition(map)
    }//.map()
    println(res)
  }
  def client={
    val client=ClientBuilder()
      .codec(RichHttp[Request](Http()))
      .dest("10.9.52.31:8082")
      .hostConnectionLimit(1)
      .name("client")
      .build()
    val request=Request()
    request.headers().add("Client_Id",UUID.randomUUID().toString)
    request.setUri("/lazystore/v1/category?params="+URLEncoder.encode("便捷生活","utf8"))
    client(request) onSuccess{response=>
      println(new String(response.content.array()))
      System.exit(0)
    }onFailure{ex=>
      ex.printStackTrace()
    }
  }
}
