package cn.changhong.lazystore.controller

import cn.changhong.lazystore.util.{Util, LazyStoreForeRouterType, LazyStoreRequestType}
import cn.changhong.web.router.RestAction
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Response, Request}
import com.twitter.util.Future

import cn.changhong.web.util._
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 * Created by yangguo on 15-1-19.
 */
object LazyStoreForeRouter extends Service[Request,Response]{
 val futurePool=ExecutorProvider.futurePool
  override def apply(request: Request): Future[Response] = {
    futurePool {
      val restRequest = RestRequest(request)
      val routers = RouterController.filterRouter(restRequest)
      val content = if (routers.isEmpty) {
        throw new RestException(RestResponseInlineCode.no_such_method, s"[${request.getUri()}] No Such Method Find!")
      } else {
        val router = routers.last
        restRequest.regex = router._1._2
        router._2(restRequest)
      }
      val response = Response()
      response.setContent(Parser.ObjectToJsonStringToChannelBuffer(content))
      response
    }
  }
}