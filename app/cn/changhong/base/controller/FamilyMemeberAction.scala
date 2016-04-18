package cn.changhong.base.controller

import java.nio.charset.Charset

import cn.changhong.lazystore.persistent.SlickDBPoolManager
import cn.changhong.base.persistent.Tables.Tables.{FamilyMember, FamilyMemberRow, User, UserRow}
import cn.changhong.base.router.{AuthAopAction, LogAopAction, RestAopAction, RestAopRouterProvider}
import cn.changhong.base.util.Parser.{FamilyMemberParser, ObjectToJsonString}
import cn.changhong.base.util._
import com.twitter.finagle.http.{Request, Response}
import org.jboss.netty.handler.codec.http.{DefaultHttpRequest, HttpMethod, HttpVersion, QueryStringDecoder}

import scala.slick.driver.MySQLDriver.simple._


/**
 *  14-12-16.
 */
object ForeFamilyMemberAction {
  def apply(request:RestRequest):Response={
    request.path match{
      case "members"=>FamilyMembersRouter(request)
      case badRouter=>NotFindActionException(badRouter)
    }
  }
}
private[controller] object FamilyMembersRouter{
  def apply(request:RestRequest): Response ={
    (request.method,request.path) match{
      case (HttpMethod.PUT,"add")=>AddMemberAction(request)
      case (HttpMethod.GET,"gets")=>GetsMemberAction(request)
      case (HttpMethod.GET,"get")=>GetMemberAction(request)
      case (HttpMethod.POST,"update")=>UpdateMemberAction(request)
      case _=>NotFindActionException(request.underlying.getUri)
    }
  }
}
private[controller] object UpdateMemberAction extends UpdateMemberAction with AuthAopAction with LogAopAction
private[controller] class UpdateMemberAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    val fmt = FamilyMemberParser(new String(request.underlying.getContent.array(), Charset.forName("utf8")))
    val res=SlickDBPoolManager.DBPool.withTransaction { implicit session =>
      FamilyMember.filter { fms => fms.userId === fmt.userId && fms.role === fmt.role}.map(x => (x.age, x.height, x.sex, x.weight)).update((fmt.age, fmt.height, fmt.sex, fmt.weight))
    }
   val content= res match{
      case 1=>RestResponseContent(RestRespCode.succeed,res)
      case t=>RestResponseContent(RestRespCode.service_inline_cause,t.toString)
    }
    DefaultHttpResponse.createResponse(content)
  }
}
private[controller] object AddMemberAction extends AddMemberAction with AuthAopAction with LogAopAction
private[controller] class AddMemberAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    try {
      val familyMember = FamilyMemberParser(new String(request.underlying.getContent.array(), Charset.forName("utf8")))
      val res = try {
        familyMember.created=new java.util.Date().getTime
        SlickDBPoolManager.DBPool.withTransaction { implicit session =>
          FamilyMember insert (familyMember)
        }
      } catch {
        case ex: Throwable => throw new RestDBException("", ex)
      }
      val content = res match {
        case 1 => RestResponseContent(RestRespCode.succeed, "创建成员成功")
        case 0 => RestResponseContent(RestRespCode.service_inline_cause, "创建成员失败")
      }
      DefaultHttpResponse.createResponse(content)
    }catch{
      case ex:Throwable=>
        ex.printStackTrace()
        throw ex
    }
  }
}
private[controller] object GetMemberAction extends GetMemberAction with AuthAopAction with LogAopAction
private[controller] class GetMemberAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    val f_id:Long=request.urlParams.getParam[Long]("f_id") match{
      case s:Seq[Long]=>s(0)
    }
    val f_role:String=request.urlParams.getParam[String]("f_role") match{
      case s:Seq[String]=>s(0)
    }
    val res=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
      FamilyMember.filter{fm=>fm.role === f_role && fm.userId === f_id}.firstOption
    }
    val content=RestResponseContent(RestRespCode.succeed,res)
    DefaultHttpResponse.createResponse(content)
  }
}
private[controller] object GetsMemberAction extends GetsMemberAction with AuthAopAction with LogAopAction
private[controller] class GetsMemberAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    val f_id:Long=request.urlParams.getParam[Long]("f_id") match{
      case s:Seq[Long]=>s(0)
    }
    val res=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
      FamilyMember.filter{fm=>fm.userId === f_id}.list
    }
    val content=RestResponseContent(RestRespCode.succeed,res)
    DefaultHttpResponse.createResponse(content)
  }
}
