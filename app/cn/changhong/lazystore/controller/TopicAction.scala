package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service.AppTopicService
import cn.changhong.base.router.RestAction
import cn.changhong.base.util.{ResponseContent, RestRequest}

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