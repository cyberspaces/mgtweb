package controllers

import backend.lazystore.persistent.T.Tables.ObjmetadataRow
import backend.lazystore.persistent.dao.{ObjMetadataDao, SqlProvider}
import backend.base.util.{BadResponseContent, Parser, ResponseContent}
import backend.lazystore.controller.Util
import play.api.mvc.{Controller, MultipartFormData}

/**
 *  15-7-13.
 */
object ApkVersionManager extends Controller{
  def newApkVersion()=AuthAction{(request,cookie)=>
    val body=request.body.asMultipartFormData
    val response=body match{
      case Some(form)=>{
        val dataParts=form.dataParts.map{kv=>val value=kv._2 match{
          case item::list=>item
          case _=>"default"
        }
          (kv._1->value)
        }
        val fileId=form.file("apk_file") match {
          case Some(file)=> {
            val (fileName,_)=createFileHandler (file, cookie)
            Some(fileName)
          }
          case None=>None
        }
        fileId match{
          case Some(id)=>{
            var internal_ver=dataParts.getOrElse("internal_ver","default")
            if(internal_ver=="default"){
              val count=SqlProvider.exec1(s"select internal_ver from appupdate where title='${dataParts.getOrElse("title","商城")}'") match{
                case item::list=>item+1
                case no=>1
              }
              internal_ver=count+""
            }
            val url=(Util.cdnFileStoreServer+"/"+id)
            val datas=dataParts+("lazyadmin_id"->cookie.getOrElse("uid","default"))+("updatedate"->System.currentTimeMillis())+("internal_ver"->internal_ver)+("downloadurls"->url)
            val keys=datas.map(_._1).reduce(_+","+_)
            val values=datas.map("'"+_._2.toString+"'").reduce(_+","+_)
            val insertSql=s"insert into appupdate($keys) values($values)"
            println(insertSql)
            ResponseContent(SqlProvider.exec(insertSql))
          }
          case None=> BadResponseContent("创建文件失败")
        }
      }
      case None=>BadResponseContent("无效的Form表单")
    }
    Ok(Parser.ObjectToJsonString(response))
  }
  def getApkLastVersion()=JsonpNoAuthAction{request=>
    val params=request.getQueryString("title")match{
      case Some(title)=>title
      case None=>"商城"
    }
    val columns=request.getQueryString("columns") match{
      case Some(cls)=>cls
      case None=>"internal_ver,publish_ver,update_info,downloadurls,updatedate"
    }
    SqlProvider.exec(s"select $columns from appupdate where title='$params' order by internal_ver desc limit 1")
  }
  private  def createFileHandler(filePart: MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile], cookie: Map[String, String]) = {
    val (filename, extensionName) = Util.getNameAndExtensionName(filePart.filename)
    val encoderFileName = Util.encoder(filename + System.currentTimeMillis())
    val newFilePath = Util.createFilePath(encoderFileName, extensionName)
    filePart.ref.moveTo(new java.io.File(newFilePath), true)
    def insertDB() = {
      val system_path = newFilePath
      val creator = cookie.getOrElse("uid", "None")
      val creation = System.currentTimeMillis()
      val checksum = "None"
      val objMetadata = ObjmetadataRow(-1, system_path, creator, creation, Some("None"))
      try {
        ObjMetadataDao.add(objMetadata)
      } catch {
        case ex: Throwable => -1
      }
    }
    val fileId = insertDB()
    val newFileName=encoderFileName + "." + extensionName
    (newFileName, fileId)
  }

}
