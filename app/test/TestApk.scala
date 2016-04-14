package test

import cn.changhong.apk.ReadApkPackageInfo
import cn.changhong.web.util.Parser
import com.google.gson.{GsonBuilder, Gson}

/**
 *  15-8-7.
 */
object TestApk {
  def mainTestApk(args: Array[String]) {
    val appInfo=ReadApkPackageInfo.getApkInfo("./Q2hBcHBTdG9yZV92MS4wLjdfMTE0Mzg5MzUyNjI1MjU=.apk")
    val gson=new GsonBuilder().create()
    println(gson.toJson(appInfo))
    println(appInfo.getDevcode+Parser.ObjectToJsonString(appInfo)+appInfo.getVersioncode+appInfo.getApkPackage)
  }
}
