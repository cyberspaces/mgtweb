package backend.lazystore.controller

import backend.lazystore.service.AppTopicService
import backend.base.router.RestAction
import backend.base.util.{ResponseContent, RestRequest}

/**
 *  15-2-4.
 */

object TopicGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = {
    AppTopicService(request)
  }
}
object TopicPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = ???
}