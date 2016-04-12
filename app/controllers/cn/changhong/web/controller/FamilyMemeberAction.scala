package cn.changhong.web.controller

import java.nio.charset.Charset

import cn.changhong.web.persistent.SlickDBPoolManager
import cn.changhong.web.persistent.Tables.Tables.{FamilyMemberRow, UserRow, FamilyMember, User}
import cn.changhong.web.router.{LogAopAction, AuthAopAction, RestAopRouterProvider, RestAopAction}
import cn.changhong.web.util.Parser.{ObjectToJsonString, FamilyMemberParser}
import cn.changhong.web.util._
import com.twitter.finagle.http.{Request, Response}
import org.jboss.netty.handler.codec.http.{QueryStringDecoder, HttpVersion, DefaultHttpRequest, HttpMethod}

import scala.slick.driver.MySQLDriver.simple._


/**
 * Created by yangguo on 14-12-16.
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
      case 1=>RestResponseContent(RestResponseInlineCode.succeed,res)
      case t=>RestResponseContent(RestResponseInlineCode.service_inline_cause,t.toString)
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
        case 1 => RestResponseContent(RestResponseInlineCode.succeed, "创建成员成功")
        case 0 => RestResponseContent(RestResponseInlineCode.service_inline_cause, "创建成员失败")
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
    val content=RestResponseContent(RestResponseInlineCode.succeed,res)
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
    val content=RestResponseContent(RestResponseInlineCode.succeed,res)
    DefaultHttpResponse.createResponse(content)
  }
}
object test{
  def main(args:Array[String]): Unit ={

//
    val user=UserRow(1,"username","phone","email","password","status","uType","bind","proto")
//    val uid=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
//      (User returning User.map(_.id)).insert(user)
//    }
////   val request=new DefaultHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET,"/")
////    request.setUri("/username?fid=1&role" +
//    val t_request=Request()
//    t_request.setUri("/username?f_id=1&f_role=121")
//    t_request.headers().set("Client_Id","")
//    val request=RestRequest(t_request)
////    println(request.getUri)
//    val f_id:Long=request.urlParams.getParam[Long]("f_id") match{
//      case s:Seq[Long]=>s(0)
//    }
//    val f_role:String=request.urlParams.getParam[String]("f_role") match{
//      case s:Seq[String]=>s(0)
//    }
////    val decoder=new QueryStringDecoder(request.underlying.getUri)
//    println(f_id+","+f_role)
//    val res=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
//      FamilyMember.filter{fm=>fm.role === f_role && fm.userId === 1L}.firstOption
//    }
//    println(ObjectToJsonString(res)+">>>")
////    val fid=decoder.getParameters.get("fid").get(0).toLong
//    val res1=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
//      FamilyMember.filter{fm=>fm.userId === f_id}.list
//    }
//   println(ObjectToJsonString(res1))
    val fm=FamilyMemberRow(user.id,Some("male"),Some(27),Some(170),Some(60.5),"bb")
    val json=ObjectToJsonString(fm)
    val fmt=FamilyMemberParser(json)
    fmt.created=new java.util.Date().getTime
    println(fmt.created)
//    val res=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
//      FamilyMember.insert(fmt)
//
//    }
//    println(res)
    val ress=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
      FamilyMember.filter{fms=>fms.userId === fmt.userId && fms.role===fmt.role}.map(x=>(x.age,x.height,x.sex,x.weight)).update((fmt.age,fmt.height,fmt.sex,fmt.weight))
    }
    println(ress)









//    val map = Map("userId" -> 1, "created" -> 34324324,"role"->13)
//    val json=ObjectToJsonString(map)
//    val family=FamilyMemberParser(json)
//
//    val res=SlickDBPoolManager.DBPool.withTransaction{implicit session=>
//      FamilyMember.insert(family)
//    }
//    println(res+","+1)
  }
}
//object Test {
//
//  import net.liftweb.json.DefaultFormats
//  import net.liftweb.json._
//  import net.liftweb.json.Extraction._
//  import net.liftweb.json.Implicits._
//  implicit val format=DefaultFormats
//  var maps:Map[Int,BigInt]=Map()
//  var step:Int=0
//  def main(args: Array[String]): Unit = {
//    //      case class FamilyMemberRow(userId: Long, sex: Option[String] = None, age: Option[Int] = None, created: Long, height: Option[Int] = None, weight: Option[Int] = None)
//    val (value,step)=new Fn()(args(0).toInt)
//
//    println(value+","+step)
//    val map = Map("userId" -> 213, "created" -> 34324324)
//    val json = ObjectToJsonString(map)
//    val familyM=FamilyMemberRow(213,None,None,34324324,None,None)
//    val jsonStr="{\"userId\":213,\"created\":34324324}"
////    println(ObjectToJsonString(familyM))
//    println(json)
////    val familym=parse(ObjectToJsonString(familyM)).extract[FamilyMemberRow]
////    val familym = FamilyMemberParser(json)
//    val familym=FamilyMemberParser(json)
//    println(familym.height + "," + familym.created + "," + familym.age + ","+familym.userId)
//
//  }
//
//}
//class Fn{
//  private[this] var map:Map[Int,BigDecimal]=Map()
//  private[this] var step:Int=0
//  def apply(index:Int):(BigDecimal,Int)={
//    step=0
//    (run(index),step)
//  }
//  private[this] def run(index:Int):BigDecimal={
//    step+=1
//    index match {
//      case 1 | 2 => 1
//      case index if index > 2 =>
//        map.get(index) match {
//          case Some(v) => v
//          case None => {
//            val t = run(index - 1) + run(index - 2)
//            map += (index -> t)
//            t
//          }
//        }
//      case index if index < 0 => throw new IllegalArgumentException("输入参数错误")
//    }
//  }
//
//}