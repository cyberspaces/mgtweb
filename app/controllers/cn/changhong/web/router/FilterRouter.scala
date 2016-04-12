package cn.changhong.web.router

/**
 * 判断是否存在爬虫->异常捕捉->设置日志->设置超时->前端路由器
 */

import java.util.UUID
import java.util.concurrent.{Executors}

import cn.changhong.lazystore.util.Util
import cn.changhong.web.controller.ForeFamilyMemberAction
import cn.changhong.web.controller.auth.ForeAuthAction
import cn.changhong.web.init.GlobalConfigFactory
import cn.changhong.web.util._
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finagle.{ Service, SimpleFilter}
import com.twitter.util._
import org.slf4j.LoggerFactory

/**
 * Created by yangguo on 14-12-8.
 *
 */
object SpiderActionInspectorFilterService extends SimpleFilter[Request,Response]{
  val logFactory=LoggerFactory.getLogger(GlobalConfigFactory.global_log_request_spider_name)
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    val remote=Util.getRealClientIp(request)
    println(remote)
    if(TokenUtil.validateIsHackAction(remote)){
      logFactory.info(remote)
      val content=RestResponseContent(RestResponseInlineCode.App_over_invocation_limit,"请不要频繁调用接口，你调用次数超过限制")
      Future.value(DefaultHttpResponse.createResponse(content,None))
    }else{
      service(request)
    }
  }
}
object ExceptionFilterService extends SimpleFilter[Request,Response]{
  val errorLog=LoggerFactory.getLogger(GlobalConfigFactory.global_log_request_error_name)
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request) handle{
      case ex:RestException=>
        val content=RestResponseContent(ex.code,ex.message)
        errorLog.error("FullErrorStack",ex)
        DefaultHttpResponse.createResponse(content,None)
      case ex:TimeoutException=>
        val content=RestResponseContent(RestResponseInlineCode.service_execution_timeout,"服务器运行超时,请稍后重试")
        errorLog.error("FullErrorStack",ex)
        DefaultHttpResponse.createResponse(content,None)
      case ex:Throwable=>
        val content=RestResponseContent(RestResponseInlineCode.service_inline_cause,ex.getMessage)
        errorLog.error("FullErrorStack",ex)
        DefaultHttpResponse.createResponse(content,None)
    }
  }
}
object AccessLogFilterService extends SimpleFilter[Request,Response]{
  lazy val requestLog=LoggerFactory.getLogger(GlobalConfigFactory.global_log_request_access_name)
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    createRequestLog(request)
    service.apply(request)
  }
  def createRequestLog(request:Request): Unit ={
    println(request.getUri())
    val tracker_id=UUID.randomUUID().toString
    request.headers().set("Tracker_Id",tracker_id)
    val value=TrackerInfo(request)
    requestLog.info(Parser.ObjectToJsonString(value))
  }
}
object TimeoutFilterService extends SimpleFilter[Request,Response]{
  implicit val javaTime=new JavaTimer()
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    service(request) within(GlobalConfigFactory.global_response_timeout)
  }
}

object ForeRouter extends Service[Request,Response]{
  lazy val futurePool=ExecutorProvider.futurePool//FuturePool(Executors.newFixedThreadPool(GlobalConfigFactory.executor_worker_max_thread_size))
  override def apply(request: Request): Future[Response] = {
    val restRequest=RestRequest(request)
    futurePool {
      restRequest.path match {
        case "auth" => ForeAuthAction(restRequest)
        case "family"=>ForeFamilyMemberAction(restRequest)
        case "user" => NotFindActionException("user")
        case "group" => NotFindActionException("group")
        case "base"=> TestBaseInfoAopAction(restRequest)
        case no_router => NotFindActionException(no_router)
      }
    }
  }
}
object TrackerInfo{
  def apply(request:Request):Map[String,String]={
    Map("ip"->request.remoteHost,"method"->request.getMethod().getName,"url"->request.getUri(),"server_id"->GlobalConfigFactory.server_id,"tracker_id"->request.headers().get("Tracker_Id"))
  }
}
