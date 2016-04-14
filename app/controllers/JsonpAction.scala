package controllers

import cn.changhong.web.util.{ResponseContent, TokenUtil, BadResponseContent, Parser}
import play.api.mvc._

/**
 *  15-5-19.
 */

object JsonpAction extends Controller {
  def apply(f: (Request[AnyContent], Map[String, String]) => AnyRef): Action[AnyContent] = {
    Action { request =>
      val cookie = request.headers.get("Cookie")
      val res=cookie match {
        case Some(s) =>
          val authInfos = parserCookie(s)
          authInfos.foreach(kv => println(kv._1 + "->" + kv._2))
          println(authInfos.get("uid"))
          val clientId = authInfos.getOrElse("cid", "default")
          val accessToken = authInfos.getOrElse("access_token", "default")
          val uid = authInfos.getOrElse("uid", "default")
          val tType = authInfos.getOrElse("tk_type", "default")
          val appdevid = authInfos.getOrElse("appdevid", "default")

          val role = authInfos.getOrElse("role", "default")
          val map = Map("uid" -> uid, "appDevId" -> appdevid, "role" -> role)

          if (authProcess(accessToken, clientId, uid, tType, accessToken)) {
            try {
              (ResponseContent(f(request,map)))
            } catch {
              case ex: Throwable =>
                ((BadResponseContent(ex.getMessage)))
            }
          } else {
            (BadResponseContent("不合法的token"))
          }
        case None => (BadResponseContent("未授权的访问"))
      }
      var content=Parser.ObjectToJsonString(res)
      val method=request.method
      if(method.toLowerCase=="get"){
        val callback=request.getQueryString("callback")
        callback match{
          case Some(cb)=>content=s"$cb(${content})"
          case None=>
        }
      }
      Ok(content)
    }
  }


  private[this] def parserCookie(cookie: String) = {
    val res = cookie.split(";").map { str =>
      val splitIndex = str.indexOf('=')
      val key = str.substring(0, splitIndex).trim
      val value = str.substring(splitIndex + 1).trim().replaceAll("\"", "")
      (key -> value)
    }
    res.toMap
  }

  private[this] def authProcess(accessToken: String, clientId: String, uid: String, tType: String, token: String) = {
    try {
      TokenUtil.validateToken(clientId, uid, tType, token)
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        false
    }
  }
}
