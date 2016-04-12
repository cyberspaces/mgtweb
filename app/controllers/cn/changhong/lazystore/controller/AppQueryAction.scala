package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service._
import cn.changhong.lazystore.util.LazyStoreRequestType
import cn.changhong.web.router.RestAction
import cn.changhong.web.util.{RestResponseInlineCode, RestException, ResponseContent, RestRequest}

/**
 * Created by yangguo on 15-2-4.
 */

object AppGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = AppGetService(request)
}
object AppsQueryAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = {
   val requestType=request.path.split("\\.").last
   requestType match{
      case LazyStoreRequestType.speity=>SpeityAppsService(request)
      case LazyStoreRequestType.top=>TopAppsService(request)
      case LazyStoreRequestType.hot=>HotApppsService(request)
      case LazyStoreRequestType.New=>NewAppsService(request)
      case LazyStoreRequestType.similar=>SimilarAppsService(request)
      case LazyStoreRequestType.search=>SearchAppsService(request)
      case s=>throw new RestException(RestResponseInlineCode.invalid_request_parameters,s"Invalid Apps Type [$s]")
    }
  }
}