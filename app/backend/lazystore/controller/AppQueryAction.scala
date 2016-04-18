package backend.lazystore.controller

import backend.lazystore.service._
import backend.lazystore.util.LazyStoreRequestType
import backend.base.router.RestAction
import backend.base.util.{RestRespCode, RestException, ResponseContent, RestRequest}

/**
 *  15-2-4.
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
      case s=>throw new RestException(RestRespCode.invalid_request_parameters,s"Invalid Apps Type [$s]")
    }
  }
}