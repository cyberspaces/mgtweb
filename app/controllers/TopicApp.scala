package controllers

import java.io.File

import _root_.cn.changhong.lazystore.persistent.dao.AppTopicDao
import _root_.cn.changhong.lazystore.util.Util
import _root_.cn.changhong.web.util.{BadResponseContent, ResponseContent, Parser}
import controllers.cn.changhong.lazystore.backup.AuthAction
import controllers.cn.changhong.lazystore.controller.Bean.TopicTemplate1
import play.api.mvc._

/**
 * Created by yangguo on 15-4-15.
 */
object TopicApplication extends Controller{
  def getAllTopicPosition=AuthAction { (request,_) =>
    val response = try {
      ResponseContent(AppTopicDao.getAllTopicPosition)
    } catch {
      case ex: Throwable => BadResponseContent(ex)
    }
    Ok(Parser.ObjectToJsonString(response))
  }
  def getTopicTemplates=AuthAction{(request,_)=>
    val response=try{
      ResponseContent(AppTopicDao.getAllTopicTemplate)
    }catch{
      case ex:Throwable=>BadResponseContent(ex)
    }

    Ok(Parser.ObjectToJsonString(response))
  }

  def getAllTopicContents=AuthAction { (request,_) =>
    var where:Map[String,String]=Map();
    val creation = request.getQueryString("sid") match {
      case Some(s) => try {
        s.trim.toLong
      } catch {
        case ex: Throwable => {
          Long.MaxValue
        }
      }
      case s => {
        Long.MaxValue
      }
    }
    request.getQueryString("template") match {
      case Some(s) => if(s.trim.length>0) where=where+("topictemplate_name"->s)
      case _ =>
    }
    request.getQueryString("position") match {
      case Some(s) => if(s.trim.length>0) where=where+("lazytopicposition_name"->s)
      case _ =>
    }
    request.getQueryString("st") match{
      case Some(s)=>if(s.trim.length>0 && s.trim.matches("\\d+")) where+=("launchtime"->s)
      case _=>
    }
    request.getQueryString("et") match{
      case Some(s)=>if(s.trim.length>0 && s.trim.matches("\\d+")) where+=("expired"->s)
      case _=>
    }
    val noHistrory=request.getQueryString("noHistory") match{
      case Some(s)=>if(s.trim=="true") true else false
      case _=>false
    }
    val size = request.getQueryString("limit") match {
      case Some(s) => try {
        s.trim.toInt
      } catch {
        case ex: Throwable => 10
      }
      case _ => 10
    }
    val columns = request.getQueryString("columns") match {
      case Some(s) => s
      case _ => "title,lazytopicposition_name,topictemplate_name,creation,launchtime,expired,topicstatus"
    }
    val isFirst=request.getQueryString("isFirst") match{
      case Some(s)=>if(s.trim=="true")true else false
      case _=>false
    }
    val offset=request.getQueryString("offset") match{
      case Some(s)=>try{
        s.trim.toInt
      }catch {
        case ex:Throwable=>0
      }
      case _=>0
    }
    val response = try {
      val whereStr=if(where.size>0) where.map{item=>
        if(item._1=="launchtime" ){
          item._1+">='"+item._2+"'"
        }else if(item._1=="expired"){
          item._1+"<='"+item._2+"'"
        }else{
          item._1+"='"+item._2+"'"
        }}.reduce(_+" and "+_) else "1=1"
      val t=AppTopicDao.getLocationTopics(whereStr,columns,noHistrory,offset,size)
      val sid=t.map { i =>
        i.get("creation").get.asInstanceOf[Long]
      }.sortBy(-_).last

      val map=Map("rows"->ResponseContent(t),"sid"->sid)
        val total=AppTopicDao.getCount(whereStr,noHistrory) match{
          case item::list=>item
          case _=> -1
        }
        map+("total"->total)
    } catch {
      case ex: Throwable => {
        Map("rows"->BadResponseContent(ex),"total"-> -1,"sid"->(Integer.MAX_VALUE))
      }
    }
    Ok(Parser.ObjectToJsonString(response))
  }

  def addTopic=AuthAction { (request, cookie) =>
    val response = request.body.asMultipartFormData match {
      case Some(form) => {
        val dataParts = form.dataParts
        val map=List("title","subtitle","refto","topictype","expired","topicstatus").map{key=>
          val value=dataParts.get(key) match {
            case Some(v)=>v match{
              case item::list=>item
              case _=>"default"
            }
            case _=>"default"
          }
          (key->value)
        }
        val action_apps = dataParts.get("appid") match {
          case Some(s)=>s
          case _=>Seq("")
        }
        val action_keywords = dataParts.get("keywords") match{
          case Some(s)=>s match{
            case item::list=>item
            case _=>""
          }
          case _=>""
        }
        val files = form.files.map { part => (part.key -> part) }.toMap
        val List(topic_img_url, action_img_url) = List("topicImg", "actionImg").map { key =>
          files.get(key) match {
            case Some(part) => {
              val path = Util.storage_file_base_path + "/" + System.currentTimeMillis() + "-" + part.filename
              part.ref.moveTo(new File(path), true)
              path
            }
            case _ => "default"
          }
        }
        val action=Parser.ObjectToJsonString(TopicTemplate1(action_img_url,action_apps,action_keywords))

        val topic=map.toMap+("creation"->System.currentTimeMillis().toString)+("img"->topic_img_url)+("lazyadmin_id"->cookie.getOrElse("uid","default"))+("action"->action)//+("creation"->System.currentTimeMillis().toString)
        ResponseContent(AppTopicDao.addTopic(topic))
      }
      case _ => BadResponseContent("Not Find MultiForm Data!")
    }
    Ok(Parser.ObjectToJsonString(response))
  }
  
  /**
   * 添加新专题接口
   * @return
   */
  def createNewTopic=AuthAction{(request,_)=>
    val form=request.body.asMultipartFormData
    form match{
      case Some(f)=>
        val dataPart=f.dataParts
        val filePart=f.files;

      case _=>
    }
    Ok("")
  }

}

