package controllers

import java.text.SimpleDateFormat

import _root_.cn.changhong.lazystore.persistent.dao.SqlProvider
import _root_.cn.changhong.web.util.{PagingResponseContent, ResponseContent, Parser, BadResponseContent}
import controllers.cn.changhong.lazystore.backup.AuthAction
import controllers.cn.changhong.lazystore.persistent.dao.PromotedAppDao
import play.api.mvc._

/**
 * Created by yangguo on 15-4-29.
 */
object PromoteAppModelApplication extends Controller{
  val default_promoted_apps_columns="id,title,description,launchtime,expired,mainposition,subposition,action,creation,status"
  val default_category_columns="name,icon,description"
  def defaultTransferFormBody(form:Map[String,Seq[String]])={
    form.map{case (k,v)=>
      (k,v.mkString)
    }
  }
  def pagingSearchPromotedApps=AuthAction { (request, map) =>
    val body = request.queryString
    val map = defaultTransferFormBody(body)
    val columns = map.getOrElse("columns", default_promoted_apps_columns)
    val offset = try {
      map.getOrElse("offset", "0").toInt
    } catch {
      case ex: Throwable => 0
    }
    val limit = try {
      map.getOrElse("limit", "10").toInt
    } catch {
      case ex: Throwable => 10
    }
    val isFirst=map.get("isFirst") match{
      case Some(s)=>if(s.toLowerCase()=="true") true else false
      case None=>false
    }
    val conditions = map.filter(kv => kv._1 != "columns" && kv._1 != "limit" && kv._1 != "offset" && kv._1!="_" && kv._1!="order" && kv._1!="isFirst")
    val where = {
      if (conditions.size > 0) conditions.map { item =>
        if (item._1 == "launchtime") {
          item._1 + ">='" + item._2 + "'"
        } else if (item._1 == "expired") {
          item._1 + "<='" + item._2 + "'"
        } else {
          item._1 + "='" + item._2 + "'"
        }
      }.reduce(_ + " and " + _)
      else "1=1"
    }
    val db_result=PromotedAppDao.pagingSelect(columns,where,limit,offset)
    val response=if(isFirst) {
      val total=PromotedAppDao.count(where)
      PagingResponseContent(db_result,total)
    } else{ResponseContent(db_result)}
    Ok(Parser.ObjectToJsonString(response))
  }
  def createdPromotedWorkSheet=addPromotedApps("草稿")
//  def draftPromotedApps=addPromotedApps("草稿")
  def updatePromotedAction=AuthAction{(request,cookie)=>
    val body=request.body.asFormUrlEncoded

    val content=body match{
      case Some(jstring)=> {
        val map=defaultTransferFormBody(jstring)
        val id=map.getOrElse("id","-1")
        val where="id='"+id+"'"
        ResponseContent(PromotedAppDao.update(map,where))
      }
      case None=>BadResponseContent("数据格式解析失败！")
    }
    Ok(Parser.ObjectToJsonString(content))
  }
  private[this] lazy val simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm")
  private[this] def addPromotedApps(status:String)=AuthAction{(request,cookie)=>
    val body=request.body.asFormUrlEncoded
    val response=body match{
      case Some(form)=> {
        val map = defaultTransferFormBody(form).map{kv=>
          val value=if(kv._1=="launchtime" || kv._1=="expired"){
            simpleDateFormat.parse(kv._2).getTime.toString
          }else kv._2.toString
          (kv._1->value)
        }
        val lazyadmin_id = cookie.getOrElse("uid", "None")
        val creation = System.currentTimeMillis().toString
        if(status=="发布"){
          val columns="count(1)"
          val where="launchtime >= '"+map.getOrElse("lauchtime","default")+"' and expired <= '"+map.getOrElse("expired","default")+"'"
          val res=(PromotedAppDao.pagingSelect(columns,where,10,0))
          if(res.size>0) BadResponseContent("已经存在相同推广区间的应用列表")
          else ResponseContent(PromotedAppDao.add(map + ("lazyadmin_id" -> lazyadmin_id) + ("creation" -> creation) + ("status" -> status)))
        }else  ResponseContent(PromotedAppDao.add(map + ("lazyadmin_id" -> lazyadmin_id) + ("creation" -> creation) + ("status" -> status)))
      }
      case None=>BadResponseContent("不是合法的form表单")
    }
    Ok(Parser.ObjectToJsonString(response))
  }
  def getCategory=AuthAction{(request,map)=>
    println(request.queryString)
    val form=defaultTransferFormBody(request.queryString)
    val params=form.getOrElse("params","0")
    val columns=form.getOrElse("columns",default_category_columns)
    val sql=s"select ${columns} from appcategories where parent='${params}' and status='release'"
    val response=SqlProvider.exec(sql)
    Ok(Parser.ObjectToJsonString(response))
  }
}

