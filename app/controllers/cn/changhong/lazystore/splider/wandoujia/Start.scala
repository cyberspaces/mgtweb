package cn.changhong.spliter.demo

import java.io.{Closeable, ByteArrayOutputStream, FileWriter, FileOutputStream}
import java.net.{HttpURLConnection, URLEncoder, URL}

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import org.jsoup.Jsoup
import org.rovak.scraper.ScrapeManager._
import org.jsoup.nodes.Element

import scala.io.Source

/**
 * Created by yangguo on 14-11-19.
 */
object Google{
  val results="#res li.g h3.r a"
  def apply(term:String)={
    "http://www.baidu.com/s?wd=" + term.replace(" ", "+")
  }
}
case class AppTag(name:String,url:String)

object ErrorLog{
  val logIn=new FileWriter("/Users/yangguo/网络爬虫/豌豆夹/error.log",true)
  def apply(content:String): Unit = {
    println(content)
    logIn.write(content + "\n")
  }
  def closeStream(stream:Closeable): Unit ={
    try{stream.close()}catch{case e=>}
  }
}
object StartT extends App{
  scrape from Google("php elephant") open{implicit  page=>
    Google.results each{x:Element=>
      val link=x.select("a[href]").attr("abs:href").substring(28)
      if(link.isValidURL) scrape from link each(x=>println("found:"+x))
    }
  }
  run
  def run= {
    val count = 20
    var start = 0
    val outStream = new FileOutputStream("/Users/yangguo/网络爬虫/豌豆夹/相机.data", true)
    try {
      while (start >= 0) {
        val url = "http://apps.wandoujia.com/api/v1/apps?tag=相机&max=" + count + "&start=" + start
        println(url)
        val conn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        conn.setConnectTimeout(5000)
        conn.getResponseCode match {
          case 200 =>
            val instream = conn.getInputStream
            val buffer: Array[Byte] = new Array[Byte](1024)
            try {
              val byteArray = new ByteArrayOutputStream()
              Iterator continually (instream.read(buffer)) takeWhile (_ > 0) foreach (byteArray.write(buffer, 0, _))
//              byteArray.write('\n')
              if (byteArray.size() < 270) {
                start = -1
                ErrorLog(url)
              } else {
                outStream.write(byteArray.toByteArray)
                start += count
              }
            } finally {
              instream.close()
            }
          case e => {
            println(e)
            start = -1
            ErrorLog(url)
          }
        }
      }
    }finally {
      ErrorLog.closeStream(outStream)
      ErrorLog.logIn.flush()
      ErrorLog.closeStream(ErrorLog.logIn)
    }
  }

//   (Source.fromURL(new URL("http://apps.wandoujia.com/api/v1/apps?tag=相机&max=60&start=100")).getLines().foreach(println))
//  val doc=Jsoup.parse(new URL("http://www.wandoujia.com/apps"),1000)
//  val li=doc.select("li.parent-cate")
//  li.toArray()foreach{e=>println(e)}
//  val it=li.iterator()
//  while(it.hasNext){
//    val e=it.next()
//    println("\n\n")
//    val ems=e.select("a[href]")
//    val eit=ems.iterator()
//    while(eit.hasNext){
//      val eits=eit.next()
//      println(eits.attr("href")+","+eits.attr("title"))
//    }
//  }
}
