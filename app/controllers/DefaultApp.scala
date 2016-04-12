//package controllers
//
//import java.io.{FileInputStream, FileOutputStream, RandomAccessFile, File}
//
//import play.api._
//import play.api.data.Form
//import play.api.data.Forms._
//import play.api.libs.iteratee.Enumerator
//import play.api.mvc._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//case class UserData(name:String,age:Int)
//
///**
// * Created by yangguo on 15-4-4.
// */
//object DefaultApp extends Controller{
//  def indexs=Action{request=>
//
//    Ok(views.html.getList("indexs"+request))
//  }
//  def hello(name:String,id:Long)=Action{
//    Ok(s"""{"name":"$name","id":$id}""")
//  }
//  def goodJob(name:String,id:Long)=Action{
//    val file=new File("/Users/yangguo/Desktop/宝贝.jpg")
//    val fileContent=Enumerator.fromFile(file)
//    Result(header = ResponseHeader(200),body = fileContent)
//  }
//  def getUsers=Action{
//    val users=(1 to 10).map{index=>User("name"+index,index,"goodJob")}
//    val htmlContext=views.html.man(users.toList)
//    println(htmlContext)
//    Ok(views.html.man(users.toList))
//  }
//  def formDemo=Action{
//    Ok(views.html.form())
//  }
//  def formPost=Action{request=>
//    println("receive data")
//    val dd=request.body.asMultipartFormData
//    println(dd+"-> log")
//    val response=dd match {
//      case Some(d)=>
//        val data=d.dataParts
//        val res=(data.seq.foldLeft("")(_+_))
//        val files=d.files.map{part=>
//          part.ref.moveTo(new java.io.File("/Users/yangguo/Desktop/00_"+part.filename),true)
//          part.key+part.filename}.foldLeft("")(_+_)
//        println(files)
//        res
//      case None=>
//        println("can't decode receive data")
//        "no receive need data"
//    }
//    Ok(response)
//  }
//  def fileDownload(filename:String)=Action { request =>
//    val file = new File("/Users/yangguo/tools.rst")
//    val total = file.length()
//    val contentRange = request.headers.get(GlobalConfiguration.Request_Header_Range)
//    val (start, end): (Long, Long) = contentRange match {
//      case Some(range) =>
//        range.replace("bytes=", "").split("-").map(_.trim).toList match {
//          case start :: end :: Nil => (start.toString.toLong, Math.min(end.toString.toLong,total))
//          case start::Nil=>(start.toString.toLong,total)
//          case l => (0, total)
//        }
//      case None => (0, total)
//    }
//    if (end < start || start > total || end > total) {
//      Ok("bad request!")
//    } else {
//      val randomAccessFile = new RandomAccessFile(file, "r")
//      val e = cn.changhong.lazystore.backup.util.fromStreamBrokePointResume(randomAccessFile, start, end)
//      Result(header = ResponseHeader(200, Map(GlobalConfiguration.Response_Header_ContentRange -> s"$start-$end/$total",GlobalConfiguration.Response_Header_ContentType->"application/octet-stream")), body = e)
//    }
//  }
//  def auth=Action{implicit request=>
//    cn.changhong.lazystore.backup.util.authAndLogHandler(Ok("handler"))
//  }
//  lazy private[this] val userForm=Form(mapping("name"->text,"age"->number)(UserData.apply)(UserData.unapply))
//}
