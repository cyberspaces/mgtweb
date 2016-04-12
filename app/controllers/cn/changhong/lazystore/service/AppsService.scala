package cn.changhong.lazystore.service

import cn.changhong.lazystore.persistent.dao.Appsdao
import cn.changhong.lazystore.service.AppsRequest
import cn.changhong.web.util._
import com.twitter.util.{Await, Future}

/**
   * Created by yangguo on 15-1-20.
*/
case class AppInfo(app:String,tags:String)
object AppGetService extends AppGetService with BaseAopService
private[service] class AppGetService extends BaseService {
  override def apply(request: RestRequest): ResponseContent = {
    val appId=request.path.split("/").last
    val tasks=Seq( ExecutorProvider.futurePool{
      Appsdao.getAppInfo(appId,AppsRequest(request))
    },ExecutorProvider.futurePool {
      Appsdao.getAppTags(appId)
    })
    val res1=Future.collect(tasks).map{resps=>
      val list=resps.map(Parser.ObjectToJsonString(_))
      AppInfo(list(0),list(1))
    }
    ResponseContent(try{
      Await.result(res1)
    }catch{
      case ex:Throwable=>throw new RestException(RestResponseInlineCode.service_execution_timeout,s"db executor time out,${ex.getMessage}")
    })
  }
}

  /**
   * 返回推荐首页APP
   */
  object SpeityAppsService extends SpeityAppsService with BaseAopService

  private[service] class SpeityAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = Appsdao.searchSpeityApps(AppsRequest(request))
      ResponseContent(content)
    }
  }


  /**
   * 热榜
   */
  object HotApppsService extends HotApppsService with BaseAopService

  private[service] class HotApppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = Appsdao.searchTopHotApps(AppsRequest(request))
      ResponseContent(content)
    }
  }

  /**
   * 总排行
   */
  object TopAppsService extends TopAppsService with BaseAopService

  private[service] class TopAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = Appsdao.searchTopSaleApps(AppsRequest(request))
      ResponseContent(content)
    }
  }

  /**
   * 根据分类查找当前分类中的推荐
   */
  object TagSpeityAppsService extends TagSpeityAppsService with BaseAopService

  private[service] class TagSpeityAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val appRequest = AppsRequest(request)
      val content = appRequest.condition match {
        case Some(s) => Appsdao.searchSpeityApps(appRequest)
        case None => throw new RestException(RestResponseInlineCode.invalid_request_parameters, "tag is null")
      }
      ResponseContent(content)
    }
  }

  /**
   * 根据分类查找当前分类最火的APP的服务
   */
  object TagTopAppsService extends TagTopAppsService with BaseAopService

  private[service] class TagTopAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val appRequest = AppsRequest(request)
      val content = appRequest.condition match {
        case Some(s) => Appsdao.searchTopHotApps(appRequest)
        case None => throw new RestException(RestResponseInlineCode.invalid_request_parameters, "tag is null");
      }
      ResponseContent(content)
    }
  }

  /**
   * 根据分类返回最新添加的APP的服务
   */
  object NewAppsService extends NewAppsService with BaseAopService

  private[service] class NewAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val appRequest = AppsRequest(request)
      val content = Appsdao.searchNewApps(appRequest)
      ResponseContent(content)
    }
  }

  /**
   * 查找与输入APP相似的APP的服务
   */
  object SimilarAppsService extends SimilarAppsService with BaseAopService

  private[service] class SimilarAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = Appsdao.searchSimilarApps(AppsRequest(request))
      ResponseContent(content)
    }
  }

  /**
   * 搜索服务
   */
  object SearchAppsService extends SearchAppsService with BaseAopService

  private[service] class SearchAppsService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = Appsdao.conditionSearchApps(AppsRequest(request))
      ResponseContent(content)
    }
  }


