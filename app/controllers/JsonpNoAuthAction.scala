package controllers

import cn.changhong.base.util.{Parser, BadResponseContent, ResponseContent}
import controllers.JsonpAction._
import play.api.mvc._

/**
 *  15-6-3.
 */
object JsonpNoAuthAction extends Controller{
  def apply(f: (Request[AnyContent]) => AnyRef): Action[AnyContent] = {
    var content:String=""
    Action { request =>
      try {
        val res = ResponseContent(f(request))
        content = Parser.ObjectToJsonString(res)

        val method = request.method
        if (method.toLowerCase == "get") {
          val callback = request.getQueryString("callback")
          callback match {
            case Some(cb) => content = s"$cb(${content})"
            case None =>
          }
        }
        println(content)
      }catch{
        case ex:Throwable=>ex.printStackTrace()
      }
      Ok(content)
    }
  }

}
