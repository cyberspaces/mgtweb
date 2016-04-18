package backend.base.controller.auth

import backend.base.router.RestAction
import backend.base.util.RestRequest
import com.twitter.finagle.http.Response
import com.twitter.util.Future

/**
 *  14-12-8.
 */
object AppAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = ???
}