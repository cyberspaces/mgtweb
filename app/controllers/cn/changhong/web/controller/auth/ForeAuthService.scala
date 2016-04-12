package cn.changhong.web.controller.auth

import cn.changhong.web.router.RestAction
import com.twitter.finagle.http.Response
import cn.changhong.web.util._

/**
 * Created by yangguo on 14-12-8.
 */
object ForeAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = {
    request.path match{
      case "app"=>AppAuthAction(request)
      case "user"=>UserAuthAction(request)
      case "3rdpart"=>ThirdPartAuthAction(request)
      case router=> NotFindActionException(router)
    }
  }
}
