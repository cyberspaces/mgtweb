package cn.changhong.lazystore.spider.demo

import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import java.net.{HttpURLConnection, URL}
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicInteger

import com.mongodb.util.JSON
import com.mongodb.{DBObject, MongoClient, Mongo}
import net.liftweb.json._
import net.liftweb.json.Serialization._

/**
 *  14-12-3.
 */
object MongoStart extends App{
  implicit val foramt=net.liftweb.json.DefaultFormats
  @volatile var count:Int=0
  def addCount(plus:Int): Int ={
    this.synchronized{
      count += plus
    }
    count
  }

  def getClassifed() {
    val collection = SpliderAllClassifed()
    val kvCollection = collection.map { classifed =>
      (classifed -> classifed.hashCode())
    }
    println(write(kvCollection))
  }
  test
  def test(): Unit = {
    val url = "http://apps.wandoujia.com/api/v1/apps?tag=小说&max=20&start=0"
    val connect=new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    connect.setConnectTimeout(5000)
    val res=connect.getResponseCode match {
      case 200=>
        val temp=new ByteArrayOutputStream()
        val bs=new Array[Byte](1024)
        val instream=connect.getInputStream
        Iterator continually(instream.read(bs)) takeWhile(_ > 0) foreach(temp.write(bs,0,_))
        instream.close()
        connect.disconnect()
        new String(temp.toByteArray,Charset.forName("utf8"))
      case e=>null
    }
    insertDataToCollection(res)
//    val json=parse(res)
//    val apps=json \ "apps"
//    val dbObjs=apps.children.map{v=>
//      val str=write(v)
//      println(str)
//      JSON.parse(str).asInstanceOf[DBObject]
//    }
//    println(dbObjs.length)
//    val db=mongo.getDB("appstore")
//    val collection=db.getCollection("test")
//    collection.insert(dbObjs:_*)
  }

  def insertDataToCollection(res:String): Unit = {
    println("total length:" + res.length)
    val json = parse(res)
    val apps = json \ "apps"
    println("Item Length="+apps.children.size)

    val dbObjs = apps.children.map { v =>
      val itemStr=compact(render(v))
      JSON.parse(itemStr).asInstanceOf[DBObject]
    }
    val mongo=new MongoClient("10.9.52.137",27017)

    try {
      println("open mongodb")
      val db = mongo.getDB("appstore1")
      val collection = db.getCollection("wandoujia")
      println("insert to db")
      collection.insert(dbObjs: _*)
      println("Insert DB Successed:"+this.addCount(dbObjs.size))
    }catch{
      case e : Throwable=>println(e.getLocalizedMessage+","+e.getMessage+","+e.getStackTrace)
    }finally {
      mongo.close()
    }
    //    }catch{case e=>println(e.getMessage+",,,,,,"+e.getLocalizedMessage)}
  }
  def lifetest: Unit ={
//    val json = ()
//    val mongo=new MongoClient("localhost",27017)
//    val db=mongo.getDB("appstore")
//    val collection=db.getCollection("wangdoujia");
//    val dbObject=JSON.parse(json).asInstanceOf[DBObject]
//    collection.insert(dbObject)
//    val cursorDoc=collection.find()
//    while(cursorDoc.hasNext) println(cursorDoc.next())
  }

}

