package controllers

import controllers.cn.changhong.lazystore.backup.AuthAction
import play.api.mvc._

/**
 * Created by yangguo on 15-4-15.
 */
object AppsApplication extends Controller{
  def addNewApp=AuthAction{(request,_)=>
    Ok("")
  }



}
