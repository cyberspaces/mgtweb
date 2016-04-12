package cn.changhong.web.util

import java.util.{Date}

import com.twitter.finagle.http.Request
import org.jboss.netty.handler.codec.http.{QueryStringDecoder, HttpRequest, HttpMethod}
import scala.collection.JavaConverters._


/**
 * Created by yangguo on 14-12-9.
 */
case class RestUrlParameters(all:java.util.Map[String,java.util.List[String]]){
  def getParam[T](name:String)(implicit parse:Parser[T]):Seq[T]={
    val values=all.get(name)
    if(values!=null) {
      values.asScala.map(parse(_))
    }else throw new MissingParameterException(RestResponseInlineCode.invalid_request_parameters,name)
  }
}
object RequestJsonBodyParams{
  def apply[T](json:String)(implicit parse:Parser[T]):T=parse(json)
}
case class LogBean(method:String,path:String,requestId:String,clientId:String,st:Long,remoteaddr:String=null,var et:Long=0,var uid:String=null)
case class RestRequest (method:HttpMethod,path:String,urlParams:RestUrlParameters,underlying:HttpRequest,logBean:LogBean,var regex:String="")
object RestRequest{
  def apply(request:Request): RestRequest ={
    val clientId=request.headers().get("Client_Id")match {
      case s: String => s
      case null=>  throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters, "Need Client Id")
    }
    val method=request.getMethod
    val decoder=new QueryStringDecoder(request.getUri)
    val path=decoder.getPath
    val params=new RestUrlParameters(decoder.getParameters)
    val requestId=request.headers().get("Tracker_Id")
    val logBean=LogBean(method.getName,decoder.getPath,requestId,clientId,new Date().getTime,request.remoteHost)
    RestRequest(method,path,params,request,logBean)
  }
}
