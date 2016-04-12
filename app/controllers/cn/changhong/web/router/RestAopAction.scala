package cn.changhong.web.router

import cn.changhong.web.util.Parser.ObjectToJsonStringToChannelBuffer
import cn.changhong.web.util._
import com.twitter.finagle.http.Response
import org.jboss.netty.handler.codec.http.{HttpResponse}
import org.slf4j.LoggerFactory



/**
 * Created by yangguo on 14-12-8.
 */
trait RestAction[Req,Rep]{
  def apply(request:Req):Rep
}
trait RestAopAction{
  def aopAction(request:RestRequest):Response
}
trait RestAopRouterProvider extends RestAction[RestRequest,Response] with RestAopAction{
  override def apply(request: RestRequest): Response = {
      aopAction(request)
  }
}

trait AuthAopAction extends RestAopAction{
  abstract override def aopAction(request:RestRequest):Response={
    println("Auth Aop Action start...")
    if(!findAccessToken(request)) throw new RestException(RestResponseInlineCode.invalid_token,"Invalid Token")
    super.aopAction(request)
  }
  def findAccessToken(request:RestRequest): Boolean ={
    val token=request.underlying.headers().get("Access_Token")
    val userId=request.underlying.headers().get("User_Id")
    val clientId=request.underlying.headers().get("Client_Id")
    val tokenType=request.underlying.headers().get("Token_Type")
    if(token == null || userId == null || clientId==null || tokenType ==null) throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters,"Invalid Auth Parameters")
    if(TokenUtil.validateToken(clientId,userId,tokenType,token)) true
    else false
  }
}
trait LogAopAction extends RestAopAction{
  val errorLog=LoggerFactory.getLogger("error")
  abstract override def aopAction(request:RestRequest):Response={
    println("log rest action start...")
    val res=super.aopAction(request)
    errorLog.info(createLog(request))
    res
  }
  def createLog(request:RestRequest): String ={
    Parser.ObjectToJsonString(request.logBean)
  }
}

object TestBaseInfoAopAction extends TestBaseInfoAopAction with AuthAopAction with LogAopAction
class TestBaseInfoAopAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    val content=RestResponseContent(RestResponseInlineCode.succeed,"base info")
    val response=Response()
    response.setContent(ObjectToJsonStringToChannelBuffer(content))
    response
  }
}
