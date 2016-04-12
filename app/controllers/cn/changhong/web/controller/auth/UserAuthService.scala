package cn.changhong.web.controller.auth

import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.persistent.Tables.Tables.User
import cn.changhong.web.router.{RestAction, RestAopRouterProvider, LogAopAction}
import cn.changhong.web.util._
import com.twitter.finagle.http.Response
import org.jboss.netty.handler.codec.http.HttpMethod

import scala.slick.driver.MySQLDriver.simple._
import cn.changhong.web.persistent.Tables.Tables._
/**
 * Created by yangguo on 14-12-8.
 */

object UserAuthAction extends RestAction[RestRequest,Response]{
  override def apply(request: RestRequest): Response = {
    (request.method,request.path) match {
      case (HttpMethod.POST, "token") => UserTokenAction(request)
      case (HttpMethod.PUT, "register") => UserRegisterTokenAction(request)
      case _ => NotFindActionException(request.underlying.getUri)
    }
  }
}
object UserTokenAction extends UserTokenAction with LogAopAction
class UserTokenAction extends RestAopRouterProvider{
  override def aopAction(request: RestRequest): Response = {
    val requestMap = Parser.ChannelBufferToJsonStringToMap(request.underlying.getContent)
    val (u_type,u_name)= AccountUtil.decodeUserAccount(requestMap.get("account"))
    val password = requestMap.get("password")
    val uType = requestMap.get("uType")
    val user = SlickDBPoolManager.DBPool.withTransaction { implicit session =>
      u_type match{
        case AccountUtil.account_login_type_email=>
          User.filter { u => u.password === password && u.email === u_name}.firstOption
        case AccountUtil.account_login_type_name=>
          User.filter { u => u.password === password && u.username === u_name}.firstOption
        case AccountUtil.account_login_type_phone=>
          User.filter { u => u.password === password && u.iphone === u_name}.firstOption
        case _=>throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters,"无效的账号类型")
      }
    }
    val content = {
      user match {
        case Some(u) =>
          if (u.utype.equals(uType.get)) {
            u.status match {
              case AccountUtil.account_status_normal | AccountUtil.account_status_inactive | AccountUtil.account_status_supervise=>
                val token = AccountUtil.createResponseToken(TokenUtil.createToken(request.logBean.clientId, u.id.toString, AccountUtil.token_type_long),u)
                RestResponseContent(RestResponseInlineCode.succeed, token)
              case AccountUtil.account_status_freeze =>
                RestResponseContent(RestResponseInlineCode.login_user_freeze, "账号暂时被冻结")
              case AccountUtil.account_status_delete=>
                RestResponseContent(RestResponseInlineCode.login_user_delete, "账号已经被删除")
              case AccountUtil.account_status_suicide=>
                RestResponseContent(RestResponseInlineCode.login_user_suicide,"账号已经注销")
            }
          } else {
            RestResponseContent(RestResponseInlineCode.invalid_request_parameters, "未找到账号类型为" + uType + "的账号")
          }
        case None => RestResponseContent(RestResponseInlineCode.user_not_exit, "不匹配任何账户")
      }
    }
    DefaultHttpResponse.createResponse(content)
  }
}
object UserRegisterTokenAction extends UserRegisterTokenAction with LogAopAction
class UserRegisterTokenAction extends RestAopRouterProvider{
  def IsAccountExist(u_type:String,u_name:String): Option[UserRow] ={
    SlickDBPoolManager.DBPool.withTransaction { implicit session =>
      u_type match{
        case AccountUtil.account_login_type_email=>
          User.filter { u => u.email === u_name}.firstOption
        case AccountUtil.account_login_type_name=>
          User.filter { u =>u.username === u_name}.firstOption
        case AccountUtil.account_login_type_phone=>
          User.filter { u => u.iphone === u_name}.firstOption
        case _=>throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters,"无效的账号类型")
      }
    }
  }
  override def aopAction(request: RestRequest): Response = {
    val registerMap = Parser.ChannelBufferToJsonStringToMap(request.underlying.getContent)
    val (u_type, u_name) = AccountUtil.decodeUserAccount(registerMap.get("account"))
    IsAccountExist(u_type, u_name) match {
      case Some(u) => throw new RestException(RestResponseInlineCode.already_user_account, "账号已经存在")
      case None => {
        val passwd = registerMap.get("password") match {
          case Some(x) => x
          case None => throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters, "密码不能为空")
        }
        val uType = registerMap.get("uType") match {
          case Some(x) => x
          case None => throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters, "注册账号类型不能为空")
        }
        val (username, email, phone) = u_type match {
          case AccountUtil.account_login_type_email =>
            val t_name = registerMap.get("account").get
            (t_name, u_name, t_name)
          case AccountUtil.account_login_type_phone =>
            val t_name = registerMap.get("account").get
            (t_name, t_name, u_name)
          case AccountUtil.account_login_type_name =>
            val t_name = registerMap.get("account").get
            (u_name, t_name, t_name)
          case _ => throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters, "无效的注册账号类型")
        }
        val user = UserRow(-100, username, phone, email, passwd, AccountUtil.account_status_normal, uType, AccountUtil.account_isbind_no, AccountUtil.promoted_type_no)
        val uid = SlickDBPoolManager.DBPool.withTransaction { implicit session =>
          (User returning User.map(_.id)).insert(user)
        }
        user.id=uid
        val token = AccountUtil.createResponseToken(TokenUtil.createToken(request.logBean.clientId, uid.toString, AccountUtil.token_type_long),user)
        val content = RestResponseContent(RestResponseInlineCode.succeed, token)
        DefaultHttpResponse.createResponse(content)
      }
    }
  }
}
object AccountUtil {
  def createResponseToken(tk:Map[String,String],user:UserRow):Map[String,String]= {
    var token = tk
    token += ("u_id"->user.id.toString)
    token += ("u_status" -> user.status)
    token += ("u_bind" -> user.bind)
    token += ("u_promoted_type" -> user.promotedType)
    token += ("u_type" -> user.utype)
    token
  }
  //账号状态
  val account_status_freeze = "freeze"//被冻结状态
  val account_status_normal = "normal"//正常状态
  val account_status_inactive="inactive"//非活动状态
  val account_status_delete = "delete"//删除状态
  val account_status_supervise = "supervise"//被监管状态
  val account_status_suicide = "suicide"//注销状态
  //账号有无绑定
  val account_isbind_yes="bind"//账号已绑定
  val account_isbind_no="nobind"//账号未绑定
  //有无实名制认证
  val promoted_type_yes="authentication"//实名认证
  val promoted_type_no="not_authentication"//未实名认证
  //账号类型
  val account_type_user="user"
  val account_type_3rdpart="3rdpart"
  val account_type_app="app"
   //token类型
  val token_type_forever="forever"//永久
  val token_type_long="long"//长时间
  val token_type_temp="temp"//临时
  val token_type_short="short"//短时间
  //账号登陆类型
  val account_login_type_email="email"
  val account_login_type_phone="phone"
  val account_login_type_name="name"

  val request_key_user_type="uType"
  val request_key_access_token="Access_Token"
  val request_key_user_id="User_Id"
  val request_key_client_id="Client_Id"

  def decodeUserAccount(account:Option[String]) :(String,String)={
    account match{
      case Some(u)=>
        val a_start=u.indexOf('_')
        if(a_start<0 || a_start == u.length-1) throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters,"无效的用户名格式")
        (u.substring(0,a_start),u.substring(a_start+1))
      case None=>throw new RestException(RestResponseInlineCode.Invalid_authorization_parameters,"无效的用户名")
    }
  }
}

