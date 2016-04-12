package cn.changhong.lazystore.util

import com.twitter.finagle.http.Request

/**
 * Created by yangguo on 15-2-3.
 */
object Util {
  val storage_file_base_path=""


  val request_key_client_id="cid"
  val request_key_app_type="st"
  val request_key_start = "start"
  val request_key_max = "max"
  val request_key_params = "params"
  val request_key_tag="tag"
  val request_key_columns="columns"
  def getRealClientIp(request:Request):String={
    println("get client Ip ...")
    List("x-forwarded-for","Proxy-Client-IP","WL-Proxy-Client-IP").map(request.headers().get(_)).filter{ip=>
      if(ip == null || ip.length == 0 || "unknown".equals(ip)) false
      else true
   } match {
     case s::list=>s
     case _=>request.remoteHost
   }
  }
}
