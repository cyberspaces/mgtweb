package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service.GetCategoryService
import cn.changhong.web.router.RestAction
import cn.changhong.web.util.{ResponseContent, RestRequest}

/**
 * Created by yangguo on 15-2-4.
 */
object CategoryGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = GetCategoryService(request)

}
