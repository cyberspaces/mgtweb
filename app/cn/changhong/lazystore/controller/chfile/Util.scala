package cn.changhong.lazystore.controller.chfile

import java.io.File
import java.util.UUID

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator

/**
 *  15-4-24.
 */
object Util {
  val cdnFileStoreServer="http://111.9.116.71:8881"
  private[this] val storagePath="/usr/share/nginx/html/lazystore/img_store"
  checkStoragePath

  private[this] def checkStoragePath={
    val file=new File(storagePath)
    if(!file.exists()) file.mkdirs()
  }

  def getStoragePath=storagePath
  def encoder(s:String)={
    UUID.randomUUID().toString
//    new sun.misc.BASE64Encoder().encode(s.getBytes())
  }
  def getNameAndExtensionName(filename:String)={
    val lastSplitIndexOf=filename.lastIndexOf('.')
    (filename.substring(0,lastSplitIndexOf),filename.substring(lastSplitIndexOf+1))

  }
  def createFilePath(name:String,extension:String)=storagePath+"/"+name+"."+extension

}
