package backend.lazystore.service

import backend.lazystore.persistent.dao.CategoryDao
import backend.base.util.{ResponseContent, RestRequest}

/**
 *  15-1-22.
 */

  /**
   * 获取类别服务
   */
  object GetCategoryService extends GetCategoryService with BaseAopService

  private[service] class GetCategoryService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = CategoryDao.getCategorys(AppsRequest(request))
      ResponseContent(content)
    }
  }






