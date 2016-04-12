package controllers.cn.changhong.lazystore.util

import com.google.gson.GsonBuilder

/**
 * Created by yangguo on 15-8-7.
 */
object JavaObjToJsonString {
  private lazy val gsonDefaultBuilder=new GsonBuilder().create()
  def apply(obj:AnyRef)=gsonDefaultBuilder.toJson(obj)
}
