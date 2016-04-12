package controllers.cn.changhong.lazystore.backup

/**
 * Created by yangguo on 15-4-13.
 */
import java.io.RandomAccessFile
import java.lang.ProcessBuilder.Redirect


import cn.changhong.web.util.{Parser, BadResponseContent, TokenUtil}
import controllers.routes
import play.api.libs.iteratee.{Enumerator, Input}
import play.api.mvc.{Request, Result}
import play.api.mvc._



import scala.concurrent._

/**
 * Created by yangguo on 15-4-7.
 */
object AuthAction extends Controller{
  def fileApply(f:(Request[Either[MaxSizeExceeded,MultipartFormData[play.api.libs.Files.TemporaryFile]]],Map[String,String])=>Result)={
    Action(parse.maxLength(1000*1024*1024,parse.multipartFormData)){request=>
      val cookie=request.headers.get("Cookie")
      println("cookie->"+cookie)
      cookie match{
        case Some(s)=>
          val authInfos=parserCookie(s)
          authInfos.foreach(kv=>println(kv._1+"->"+kv._2))
          println(authInfos.get("uid"))
          val clientId=authInfos.getOrElse("cid","default")
          val accessToken=authInfos.getOrElse("access_token","default")
          val uid=authInfos.getOrElse("uid","default")
          val tType=authInfos.getOrElse("tk_type","default")
          val appdevid=authInfos.getOrElse("appdevid","default")

          val role=authInfos.getOrElse("role","default")
          val map=Map("uid"->uid,"appDevId"->appdevid,"role"->role)
          if(authProcess(accessToken ,clientId,uid,tType,accessToken)) {
            try{
              f(request,map).withHeaders(("Access-Control-Allow-Origin"->"*"),("Access-Control-Allow-Methods"->"POST"),("Access-Control-Max-Age"->"300"),("Access-Control-Allow-Headers"->"Origin, X-Requested-With, Content-Type, Accept"))
            }catch {
              case ex:Throwable=>
                ex.printStackTrace()
                println("sadsadsadsa")
                Ok(Parser.ObjectToJsonString(BadResponseContent(ex.getMessage)))
            }
          }else{
            println(">>>>>>>>>>>>>>>")
            Redirect(routes.Application.index())
          }
        case None=> Ok(Parser.ObjectToJsonString(BadResponseContent("未授权的访问")))
      }
    }
  }
  def apply(f:(Request[AnyContent],Map[String,String])=>Result):Action[AnyContent]={
    Action{request=>
      val cookie=request.headers.get("Cookie")
      println("cookie->"+cookie)
      cookie match{
        case Some(s)=>
          val authInfos=parserCookie(s)
          authInfos.foreach(kv=>println(kv._1+"->"+kv._2))
          println(authInfos.get("uid"))
          val clientId=authInfos.getOrElse("cid","default")
          val accessToken=authInfos.getOrElse("access_token","default")
          val uid=authInfos.getOrElse("uid","default")
          val tType=authInfos.getOrElse("tk_type","default")
          val appdevid=authInfos.getOrElse("appdevid","default")

          val role=authInfos.getOrElse("role","default")
          val map=Map("uid"->uid,"appDevId"->appdevid,"role"->role)
          if(authProcess(accessToken ,clientId,uid,tType,accessToken)) {
            try{
              f(request,map).withHeaders(("Access-Control-Allow-Origin"->"*"),("Access-Control-Allow-Methods"->"POST"),("Access-Control-Max-Age"->"300"),("Access-Control-Allow-Headers"->"Origin, X-Requested-With, Content-Type, Accept"))
            }catch {
              case ex:Throwable=>
                ex.printStackTrace()
                println("sadsadsadsa")
                Ok(Parser.ObjectToJsonString(BadResponseContent(ex.getMessage)))
            }
          }else{
            println(">>>>>>>>>>>>>>>")
            Redirect(routes.Application.index())
          }
        case None=> Ok(Parser.ObjectToJsonString(BadResponseContent("未授权的访问")))
      }
    }
  }


  private[this] def parserCookie(cookie:String)={
    val res=cookie.split(";").map{str=>
      val splitIndex=str.indexOf('=')
      val key=str.substring(0,splitIndex).trim
      val value=str.substring(splitIndex+1).trim().replaceAll("\"","")
      (key->value)
    }
    res.toMap
  }
  private[this] def authProcess(accessToken:String,clientId:String,uid:String,tType:String,token:String)={
    try{
      TokenUtil.validateToken(clientId,uid,tType,token)
    }catch {
      case ex:Throwable=>
        ex.printStackTrace()
        false
    }
  }

}
object util extends Controller{

//  def authAndLogHandler[A](fn: =>Result)(implicit request:play.api.mvc.Request[A]): Result ={
//    request.headers.get(GlobalConfiguration.Request_Header_Auth_Key) match{
//      case Some(auth)=>Ok(views.html.noauth("认证成功"))
//      case None=> Ok(views.html.noauth("请求头中不包含认证信息"))
//    }
//  }
  /**
   * 实现文件的断点续传
   * @param input 输入的随机读写文件
   * @param start offset
   * @param end end length
   * @param chunkSize block size
   * @param ec
   * @return
   */
  def fromStreamBrokePointResume(input:RandomAccessFile,start:Long,end:Long,chunkSize:Int=1024*1)(implicit ec:ExecutionContext): Enumerator[Array[Byte]]= {
    implicit val pec = ec.prepare()
    input.seek(start)
    var remains: Int = 0
    Enumerator.generateM({
      val buffer = new Array[Byte](chunkSize)
      remains = Math.min(chunkSize,end-input.getFilePointer()).toInt

      val bytesRead = blocking {
        input.read(buffer, 0, remains)
      }
      val chunk = bytesRead match {
        case -1 | 0=> None
        case `chunkSize` => Some(buffer)
        case read =>
          val input = new Array[Byte](read)
          System.arraycopy(buffer, 0, input, 0, read)
          Some(input)
      }
      Future.successful(chunk)
    })(pec).onDoneEnumerating(input.close)(pec)
  }
}
