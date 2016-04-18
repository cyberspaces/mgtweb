package cn.changhong.lazystore.controller

import java.io.File
import java.util.UUID

import com.twitter.finagle.http.Request

/**
 *  15-4-24.
 */
object Util {
  val cdnFileStoreServer="http://us.newasst.com:8085"
  private[this] val storagePath="/usr/soft/newasst/imgstore"

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

  checkStoragePath

  private[this] def checkStoragePath={
    val file=new File(storagePath)
    if(!file.exists()) file.mkdirs()
  }

  def getStoragePath=storagePath

  def encoder(s:String)={
    UUID.randomUUID().toString
  }

  def getNameAndExtensionName(filename:String)={
    val lastSplitIndexOf=filename.lastIndexOf('.')
    (filename.substring(0,lastSplitIndexOf),filename.substring(lastSplitIndexOf+1))
  }

  def createFilePath(name:String,extension:String)=storagePath+"/"+name+"."+extension

}
