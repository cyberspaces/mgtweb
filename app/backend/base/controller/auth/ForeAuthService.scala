package cn.changhong.base.controller.auth

import cn.changhong.base.router.RestAction
import com.twitter.finagle.http.Response
import cn.changhong.base.util._

/**
 *  14-12-8.
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
