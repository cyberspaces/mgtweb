package cn.changhong.spliter.demo

import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.concurrent.{FutureTask, Callable, Future, Executors}

import org.jsoup.Jsoup

/**
 * Created by yangguo on 14-11-19.
 */
case class sAppTag(name:String,url:String)
case class Classified(parent:AppTag,childs:List[AppTag])
object SpliderAllClassifed{
  def apply()={
    var classifieds:List[Classified]=List()
    val doc=Jsoup.parse(new URL("http://www.wandoujia.com/apps"),3000)
    val allClassifieds=doc.select("li.parent-cate")
    val allIt=allClassifieds.iterator()
    while(allIt.hasNext){
      val classified=allIt.next()
      val allChild=classified.select("a[href]")
      val cIt=allChild.iterator()
      allChild.toArray()
      var first=true
      var childs:List[AppTag]=List()
      var parent:AppTag=null
      while(cIt.hasNext) {
        val child = cIt.next()
        val app = AppTag(child.attr("title"), child.attr("href"))
        if (first) {
          first = false
          parent=app
        } else {
          childs= app :: childs
        }
      }
      val item=Classified(parent,childs)
      classifieds=item :: classifieds
    }
    classifieds
  }
}
object Start extends App{
  val types=SpliderAllClassifed()
  val task=types.flatMap { c =>
    c.childs.map{t=>
      new SpliterTask(t,c.parent.name)
    }
  }
  val res=task.map { item =>
    Util.doTask(item)
  }
}

class SpliterTask(tag:AppTag,parentTag:String)extends Callable[Int]{
  override def call(): Int = {
    try {
      run
      1
    } catch {
      case e =>
        Util.log(e.getLocalizedMessage+"\n")
        -1
    }
  }

  def run(): Unit = {
    val tagPath=Util.createDir(parentTag)
    val byteArray = new ByteArrayOutputStream()
    val buffer: Array[Byte] = new Array[Byte](1024)
    try{
      val count=20
      var start=0
      while(start>=0){
        val url = "http://apps.wandoujia.com/api/v1/apps?tag="+tag.name+"&max=" + count + "&start=" + start
        println(url)
        val conn = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        conn.setConnectTimeout(5000)
        conn.getResponseCode match {
          case 200 =>
            val instream = conn.getInputStream
            try {
              byteArray.reset()
              Iterator continually (instream.read(buffer)) takeWhile (_ > 0) foreach (byteArray.write(buffer, 0, _))
              if (byteArray.size() < 270) {
                start = -1
                ErrorLog(url)
              } else {
                val json=new String(byteArray.toByteArray)
                println(tag.name+count+start+json.length)
                MongoStart.insertDataToCollection(json)
                start += count
              }
            } finally {
              Util.closeStream(instream)
            }
          case e => {
            start = -1
            ErrorLog(url)
          }
        }
      }
    }finally {
//      Util.closeStream(outStream)
    }
  }
}
object Util{
  val executor=Executors.newFixedThreadPool(4)
  val basePath="/Users/yangguo/网络爬虫/豌豆夹/"
  val errorStream=new FileWriter(basePath+"error.log")

  def log(content:String){
    errorStream.write(content+"\n")
    errorStream.flush()
  }
  def createDir(tag:String): String ={
    val path=basePath+tag
    val file=new File(path)
    if(!file.exists()) file.mkdirs()
    path
  }
  def closeStream(stream:Closeable): Unit ={
    try{stream.close()}catch {case e=>}
  }
  def doTask[T](task:Callable[T]):Future[T]={
    val future=new FutureTask[T](task)
    executor.submit(future)
    future
  }
}




