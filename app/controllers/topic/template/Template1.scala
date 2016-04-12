package controllers.topic.template

import controllers.cn.changhong.lazystore.backup.AuthAction
import play.api._
import play.api.mvc._


/**
 * Created by yangguo on 15-4-23.
 */
case class Template1Bean(url:String,objs:Seq[Long])
object Template1 extends Controller {
  def creatTemplateModel=AuthAction{(request,_)=>
    Ok(createHtml).as("text/html")
  }
  private[this] def createHtml={
    ""
  }
}

