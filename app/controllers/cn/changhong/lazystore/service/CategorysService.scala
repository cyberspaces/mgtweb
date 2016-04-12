package cn.changhong.lazystore.service

import cn.changhong.lazystore.persistent.dao.CategoryDao
import cn.changhong.web.util.{ResponseContent, RestRequest}

/**
 * Created by yangguo on 15-1-22.
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






