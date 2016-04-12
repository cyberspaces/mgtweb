package cn.changhong.lazystore.persistent.dao

import cn.changhong.lazystore.service.AppsRequest
import cn.changhong.web.util.{RestResponseInlineCode, RestException}

import SqlProvider._
/**
 * Created by yangguo on 15-1-22.
 */
object CategoryDao {
  private[this] val T_APPCATEGORIES ="appcategories"
  private[this] val c_category_parent="parent"


  def getCategorys(request:AppsRequest)= {

    val columns = request.columns match {
      case Some(s) => s"$s,id as sid"
      case None => "*,id as sid"
    }
    val parent = request.condition match {
      case Some(s) => s
      case None => "0"
    }
    val sql = request.condition match {
      case Some(s) => s"select $columns from $T_APPCATEGORIES where $c_category_parent = '$parent' "
      case None => throw new RestException(RestResponseInlineCode.invalid_request_parameters, "请输入类别")
    }
    exec(sql)
  }
}
