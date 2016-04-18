package cn.changhong.base.controller.auth

import cn.changhong.base.router.RestAction
import cn.changhong.base.util.RestRequest
import com.twitter.finagle.http.Response

/**
 *  14-12-8.
 */
object ThirdPartAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = ???
}
