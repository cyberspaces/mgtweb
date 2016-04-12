package cn.changhong.web.util

import com.twitter.finagle.http.Response
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpResponseStatus}
import org.jboss.netty.handler.codec.http.HttpResponseStatus._

/**
 * Created by yangguo on 14-12-9.
 */
case class RestException(code:Int,message:String,ex:Throwable=null,status:HttpResponseStatus=BAD_REQUEST) extends RuntimeException(message){
  def postProcess(response:HttpResponse):Unit=()
}
class MissingParameterException(code:Int,parameterName:String) extends RestException(code,"Missing required parameter:"+parameterName)
class InvalidParameterFormatException(code:Int,srcValue:String,targetType:String,error:String) extends RestException(code,"Invalid Transfer String ["+srcValue+"] to type ["+targetType+"],Error Message ["+error+"]")
class JsonEncoderException(code:Int,srcType:String,error:String) extends RestException(code,"Invalid Transfer ["+srcType+"] To Json String,Error Message ["+error+"]")
class NotFindActionException(url:String,error:String) extends RestException(RestResponseInlineCode.no_such_method,"Not Find Method ["+url+"],Error Message["+error+"]")
class NoAuthException(url:String,error:String) extends RestException(RestResponseInlineCode.permission_need,"This Method ["+url+"] Need User Permission,Error Message["+error+"]")
class RestDBException(message:String,ex:Throwable) extends RestException(RestResponseInlineCode.service_inline_cause,message,ex)
class RestCtlException(message:String,ex:Throwable) extends RestException(RestResponseInlineCode.service_inline_cause,message,ex)
object NotFindActionException{
  def apply(url:String,error:String="No Find Router"): Response ={
    throw new NotFindActionException(url,error)
  }
}
