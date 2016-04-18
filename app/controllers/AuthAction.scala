package controllers

/**
 *  15-4-13.
 */
import backend.base.util.{BadResponseContent, Parser, TokenUtil}
// Redirect(routes.Application.index()) , don't remove.
import play.api.mvc.{Request, Result, _}

/**
 *  15-4-7.
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
                Ok(Parser.ObjectToJsonString(BadResponseContent(ex.getMessage)))
            }
          }else{
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
                Ok(Parser.ObjectToJsonString(BadResponseContent(ex.getMessage)))
            }
          }else{
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
