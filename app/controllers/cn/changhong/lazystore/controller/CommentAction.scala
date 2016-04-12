package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service.{AddAppCommentService, GetAppCommentService}
import cn.changhong.lazystore.util.{LazyStoreRequestType, Util}
import cn.changhong.web.router.RestAction
import cn.changhong.web.util.{RestResponseInlineCode, RestException, ResponseContent, RestRequest}
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 * Created by yangguo on 15-2-4.
 */



object AppCommentGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = GetAppCommentService(request)
}
object AppCommentStarGetAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = GetAppCommentService(request)
}

object AppCommentPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent = AddAppCommentService(request)
}
///**
// * 评论相关
// */
//object CommentAction extends RestAction[RestRequest,ResponseContent]{
//  override def apply(request: RestRequest): ResponseContent = {
//    val requestType=try {
//      request.urlParams.getParam[String](Util.request_key_app_type) match {
//        case s :: Nil => Some(s)
//        case _ => throw new RestException(RestResponseInlineCode.invalid_request_parameters, "Needs Comment")
//      }
//    } catch{case _=>None}
//    requestType match {
//      case None =>
//        if (request.method.equals(HttpMethod.PUT)) {
//          UserCommentService.AddAppCommentService(request)
//        } else throw new RestException(RestResponseInlineCode.no_such_method, "此方法不存在")
//      case Some(s) =>
//        s match {
//          case LazyStoreRequestType.star => UserCommentService.GetAppCommentStarService(request)
//          case LazyStoreRequestType.comment => UserCommentService.GetAppCommentService(request)
//          case t => throw new RestException(RestResponseInlineCode.invalid_request_parameters, s"type=$t 错误")
//        }
//    }
//  }
//}
