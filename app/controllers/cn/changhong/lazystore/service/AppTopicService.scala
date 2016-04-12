package cn.changhong.lazystore.service

import cn.changhong.lazystore.persistent.dao.AppTopicDao
import cn.changhong.lazystore.service.AppsRequest
import cn.changhong.web.util._
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 * Created by yangguo on 15-1-23.
 */
object AppTopicService extends AppTopicService with BaseAopService
class AppTopicService extends BaseService{
  override def apply(request: RestRequest): ResponseContent = {
//    val appsRequest=AppsRequest(request)
    val content=request.method match{
      case HttpMethod.GET=>AppTopicDao.getAppTopics(AppsRequest(request))
      case HttpMethod.POST=>null
      case HttpMethod.PUT=>AppTopicDao.addAppTopic(request)
      case HttpMethod.DELETE=>null
      case _=>throw new RestException(RestResponseInlineCode.no_such_method,"未找到此服务")
    }
    ResponseContent(content)
  }
}
