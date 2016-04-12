package cn.changhong.web.controller.auth

import cn.changhong.web.router.RestAction
import cn.changhong.web.util.RestRequest
import com.twitter.finagle.http.Response
import com.twitter.util.Future

/**
 * Created by yangguo on 14-12-8.
 */
object AppAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = ???
}