package backend.lazystore.controller

import backend.lazystore.service.GetCategoryService
import backend.base.router.RestAction
import backend.base.util.{ResponseContent, RestRequest}

/**
 *  15-2-4.
 */
object CategoryGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = GetCategoryService(request)

}
