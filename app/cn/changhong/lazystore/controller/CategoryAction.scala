package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service.GetCategoryService
import cn.changhong.base.router.RestAction
import cn.changhong.base.util.{ResponseContent, RestRequest}

/**
 *  15-2-4.
 */
object CategoryGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = GetCategoryService(request)

}
