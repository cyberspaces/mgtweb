package cn.changhong.web.controller.auth

import cn.changhong.web.router.RestAction
import cn.changhong.web.util.RestRequest
import com.twitter.finagle.http.Response

/**
 * Created by yangguo on 14-12-8.
 */
object ThirdPartAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = ???
}
