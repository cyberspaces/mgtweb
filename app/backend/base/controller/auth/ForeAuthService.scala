package backend.base.controller.auth

import backend.base.router.RestAction
import com.twitter.finagle.http.Response
import backend.base.util._

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
