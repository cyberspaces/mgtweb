package cn.changhong.lazystore.controller

import cn.changhong.lazystore.util.{LazyStoreForeRouterType, LazyStoreRequestType}
import cn.changhong.base.router.RestAction
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Response, Request}
import com.twitter.util.Future

import cn.changhong.base.util._
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 *  15-1-19.
 */
object LazyStoreForeRouter extends Service[Request,Response]{
 val futurePool=ExecutorProvider.futurePool
  override def apply(request: Request): Future[Response] = {
    futurePool {
      val restRequest = RestRequest(request)
      val routers = RouterController.filterRouter(restRequest)
      val content = if (routers.isEmpty) {
        throw new RestException(RestRespCode.no_such_method, s"[${request.getUri()}] No Such Method Find!")
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