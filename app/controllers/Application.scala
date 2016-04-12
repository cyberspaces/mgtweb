package controllers


import java.io.File
import java.text.{SimpleDateFormat, DateFormat}
import java.util.concurrent.atomic.AtomicLong

import _root_.cn.changhong.apk.ApkInfo
import _root_.cn.changhong.apk.ReadApkPackageInfo

import _root_.cn.changhong.lazystore.persistent.T.Tables.ObjmetadataRow
import _root_.cn.changhong.lazystore.persistent.dao.{SqlProvider, AppTopicDao, ObjMetadataDao, AdminAcountDao}
import _root_.cn.changhong.web.util.{BadResponseContent, ResponseContent, Parser, TokenUtil}
import controllers.cn.changhong.lazystore.backup.AuthAction
import controllers.cn.changhong.lazystore.controller.chfile.Util
import controllers.cn.changhong.lazystore.util.JavaObjToJsonString
import play.api.mvc._

import scala.io.Source

object Application extends Controller {

  private lazy val default_storage_service_url="http://111.9.116.71:8881"
  private lazy val idGenerator=new AtomicLong(1)
  private def createId=(System.currentTimeMillis()+idGenerator.getAndIncrement()).toString
  def createNewApk=AuthAction{(request,cookie)=>

    val _form=request.body.asFormUrlEncoded
    println(_form)

    val res=_form match {
      case Some(tempForm)=>{
        val form=PromoteAppModelApplication.defaultTransferFormBody(tempForm)
        def getValues(key:String)=form.getOrElse(key,"default")
        val apppkgColumns=List(
          "downloadurls",
          "pkgsize",
          "devcode",
          "versioncode",
          "maxsdkversion",
          "minsdkversion",
          "targetsdkversion",
          "permissionstatement",
          "dangerouspermissions",
          "screenshots",
          "changelogs",
          "adminmsg"
        )
        val lazyappColumns=List("title","icon","packagename","described")
        val appdevColumns=List("email","company","website","intro","address","c_described","b_name","b_phone","b_email","b_qq","b_licence",
          "o_id","t_id","c_id","b_id")
//        val apptagsColumns=List("tags")

        val title=getValues("title")

        println("title->"+title)
        val packagename=getValues("packagename")
        val _sql=s"select id,appdev_id from lazyapp where title='$title' or packagename='$packagename'"

        var apppkgKVs:List[(String,String)]=apppkgColumns.map{key=>(key->getValues(key))}
        var sqls:List[(Int,String)]=List()

        var downloadUrls=getValues("downloadurls")

        downloadUrls="[{\"url\":\""+downloadUrls+"\"}]"
        apppkgKVs=apppkgKVs

        var icon = List("px24","px78","px48","px68","px100").map(key=>"\""+key+"\":\""+getValues("icon")+"\"").reduce(_+","+_)
        icon="{"+icon+"}"

        SqlProvider.exec(_sql) match {
          case item::list=>{
            val lazyapp_id=item.getOrElse("id","0").toString
            val appdev_id=item.getOrElse("appdev_id","0").toString
            val apppkg_id=createId

            var appdevKVs=appdevColumns.map(key=>(key->getValues(key)))
            appdevKVs=appdevKVs


            sqls=(1->createUpdateSql(appdevKVs.toMap,"appdev",s"id='$appdev_id'"))::sqls
            val lazyappMap=lazyappColumns.map(key=>(key->getValues(key))).toMap+("icon"->icon)+("last_apppkg_id"->apppkg_id)+("updateddate"->apppkg_id)
            val lazyAppUpdateSql=(2->createUpdateSql(lazyappMap,"lazyapp",s"id=${lazyapp_id}"))
            sqls=lazyAppUpdateSql::sqls


//created app tag information
            var index=10
            val tagSqls=getValues("tags").split(",").toList.filter{tag=>
              val list=SqlProvider.exec1( s"select count(1) from apptags where tag='${tag}' and lazyapp_title='${title}' and lazyapp_id='${lazyapp_id}'")
              if(null!=list&&list.size>0&&list(0)>0) false
              else true
            }.map(tag=>
              List(("lazyapp_id"->lazyapp_id),("lazyapp_title"->title),("tag"->tag),("weight"->"1000"))
            ).map{item=>
              index=index+1
              (index->createInsertSql(item.toMap,"apptags"))
            }
            sqls=tagSqls:::sqls

            //p
            apppkgKVs=("id"->apppkg_id):: ("creation" -> lazyapp_id) ::("md5"->"default")::("lazyapp_id"->lazyapp_id)::("pkgstatus"->"release") ::("publishdate"->s"${System.currentTimeMillis()}"):: ("securitystatus"->"safe")::("lazyadmin_id"->s"${cookie.getOrElse("uid","1423548742269")}")::("source"->"官网")::apppkgKVs

          }
          case _=> {
            val lazyapp_id = createId
            val apppkg_id = createId
            val appdev_id = createId
            //created app developer information
            var appdevKVs = appdevColumns.map(key => (key -> getValues(key)))
            appdevKVs = ("id" -> appdev_id) :: appdevKVs
            sqls = (1->createInsertSql(appdevKVs.toMap, "appdev")) :: sqls
            //created lazyapp information



            val lazyappKVs = ("status"->"release")::("creation" -> lazyapp_id) :: ("updateddate" -> lazyapp_id) :: ("id" -> lazyapp_id) :: ("appdev_id" -> appdev_id) :: ("last_apppkg_id" -> apppkg_id) :: ("apptype" -> "changhong.android.app") :: ("icon" -> icon) :: lazyappColumns.map(key => (key -> getValues(key)))
            val lazyappMap=lazyappKVs.toMap+("icon"->icon)

            sqls = (2->createInsertSql(lazyappMap, "lazyapp")) :: sqls

            //created apppkg information
            apppkgKVs = ("id" -> apppkg_id) :: ("creation" -> lazyapp_id)::("md5"->"default") :: ("lazyapp_id" -> lazyapp_id) :: ("pkgstatus" -> "release") :: ("publishdate" -> s"${System.currentTimeMillis()}") :: ("securitystatus" -> "safe") :: ("lazyadmin_id" -> s"${cookie.getOrElse("uid", "1423548742269")}") :: ("source" -> "官网") :: apppkgKVs

            //created app tag information
            var index=10
            val tagSqls=getValues("tags").split(",").toList.map(tag=>List(("lazyapp_id"->lazyapp_id),("lazyapp_title"->title),("tag"->tag),("weight"->"1000"))).map{item=>
              index=index+1
              (index->createInsertSql(item.toMap,"apptags"))
            }
            sqls=tagSqls:::sqls
          }
        }
        val _apppkgMap=apppkgKVs.toMap+("downloadurls"->downloadUrls)
        sqls=(3->createInsertSql(_apppkgMap,"apppkg"))::sqls
        ResponseContent(SqlProvider.execTransaction(sqls))
      }
      case None=>BadResponseContent("Form表单格式不对")
    }
    Ok(Parser.ObjectToJsonString(res))
  }
  def createInsertSql(map:Map[String,String],tablename:String):String={
    val keys=map.map(_._1).reduce(_+","+_)
    val values=map.map(kv=>"'"+kv._2+"'").reduce(_+","+_)
    s"insert into $tablename($keys) values($values)"
  }
  def createUpdateSql(map:Map[String,String],tablename:String,where:String):String={
    val settings=map.map(kv=>kv._1+"='"+kv._2+"'").reduce(_+","+_)
    s"update $tablename set $settings where $where"
  }
  def logout=AuthAction{(request,_)=>

    Ok(Parser.ObjectToJsonString(ResponseContent("0")))
  }
  def testHtml(path: String = "public/index.html") = Action {
    val file = new File(path)
    println(file.exists().toString + path)
    Ok(Source.fromFile(file).mkString).withHeaders(("Access-Control-Allow-Origin" -> "*"), ("Access-Control-Allow-Methods" -> "POST")).as("text/html")
  }

  //
  //  def testTopicTemplateModule=Action{
  ////    Ok(views.html.topic_template.template_01("hello"))
  //  }
  def h5TopicModuleInstance(id: String) = Action { request =>
    val view = id match {
      case "1" => {
        var imgUrl = ""
        var data: List[Map[String, String]] = List(Map())
        views.html.topic_template.template_01(imgUrl, data)
      }
      case _ => views.html.index("")
    }
    Ok(view)
  }

  def index = Action {
    Ok("")
  }

  def login = Action { request =>
    val remoteAddress = TokenUtil.generateToken(request.remoteAddress + "_" + System.currentTimeMillis())
    val form = request.body.asFormUrlEncoded //.getOrElse(Map())

    var username: String = "default"
    var passwd: String = "default"
    form match {
      case Some(map) =>
        val tempMap = map.map { kv =>
          println(kv._2.head)
          val value = kv._2.toList match {
            case item :: list => item
            case _ => "default"
          }
          (kv._1 -> value)
        }
        username = tempMap.getOrElse("username", "default")
        passwd = tempMap.getOrElse("password", "default")
      case _ =>
    }
    val user = AdminAcountDao.selectAdmin(username, passwd)
    user match {
      case item :: list =>
        val clientId = remoteAddress
        val cookies = item.map { kv =>
          val key = if (kv._1 == "id") "uid"
          else kv._1
          Cookie(key, kv._2.toString, Some(86400))
        }.toList
        val accessTokenCookies = TokenUtil.createToken(clientId, item.getOrElse("id", System.currentTimeMillis().toString).toString, "pm").map { kv => Cookie(kv._1, kv._2.toString, Some(86400)) }.toList
        val appDevIdCookie = Cookie("appdevid", "1423649636951", Some(86400))
        val clientIdCookie = Cookie("cid", clientId, Some(86400))
        Ok(Parser.ObjectToJsonString(ResponseContent(item.getOrElse("name", "黑户").toString))).withCookies((appDevIdCookie :: clientIdCookie :: cookies ++ accessTokenCookies): _*)
      case _ => Ok(Parser.ObjectToJsonString(BadResponseContent("用户名或者密码错误！")))
    }
  }

  def main(name: String) = AuthAction { (request, _) =>
    Ok(views.html.main(name))
  }

  def account = AuthAction { (request, _) =>
    Ok(views.html.account.account())
  }

  def allAccount = AuthAction { (request, _) =>
    val response = (AdminAcountDao.selectAll)
    Ok(Parser.ObjectToJsonString(Map("rows" -> ResponseContent(response), "total" -> response.length, "start" -> 0)))
  }

  def newAdmin = AuthAction { (request, map) =>
    val response = if (!map.getOrElse("role", "default").equals("admin")) {
      BadResponseContent("没有权限创建新用户!")
    } else {
      ResponseContent(request.body.asFormUrlEncoded match {
        case Some(s) =>
          try {
            val data = s.map { kv =>
              (kv._1, if (kv._2.length > 0) kv._2.last else "default")
            }
            AdminAcountDao.addAdmin(data) match {
              case item :: list => item.toString
              case _ => "-1"
            }
          } catch {
            case ex: Throwable =>
              ex.printStackTrace()
              "-1"
          }
        case res => {
          println(res)
          "-1"
        }
      })
    }
    Ok(Parser.ObjectToJsonString(response))
  }

  def deleteAdmin(id: String) = AuthAction { (request, _) =>
    val response = try {
      AdminAcountDao.deleteAdmin(id) match {
        case item :: list => item.toString
        case _ => "-1"
      }
    } catch {
      case ex: Throwable =>
        ex.printStackTrace()
        "-1"
    }
    Ok(Parser.ObjectToJsonString(ResponseContent(response)))
  }

  def getTopics = AuthAction { (request, _) =>
    Ok("")
  }

  def jsonpDemo = Action { request =>
    val callback = request.getQueryString("callback") match {
      case Some(s) => s
      case None => "callback"
    }
    Ok(callback + "({hello:1})")
  }

  def createFileHandler(filePart: MultipartFormData.FilePart[play.api.libs.Files.TemporaryFile], cookie: Map[String, String]) = {
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
    val res=if(extensionName=="apk") {
      val apkInfo = try {
        println("new File Path:"+newFilePath)
        ReadApkPackageInfo.getApkInfo(newFilePath)
      } catch {
        case ex: Throwable =>
          ex.printStackTrace()
          new ApkInfo()
      }
      apkInfo.setDownloadurls(newFileName)
      JavaObjToJsonString(apkInfo)
    } else{
      newFileName
    }
    println("upload file:"+res)
    (res, fileId)
  }

  private def createDownloadUrls(url:String):String={
    ""
  }


  def uploadFile = AuthAction.fileApply((request, cookie) => {
    val response=request.body match {
      case Left(MaxSizeExceeded(length)) => BadResponseContent("上传文件超过长度！")
      case Right(form) => {
        val res = form.files.map { filePart =>
          val (newFileName ,_) = createFileHandler(filePart, cookie)
          newFileName
        }
        ResponseContent(res)
      }
    }
    Ok(Parser.ObjectToJsonString(response))
  })

  def createReleaseTopic = AuthAction((request, cookie) => {
    val response = createTopic(request, cookie, "发布")
    Ok(Parser.ObjectToJsonString(response))
  })

  def createDraftTopic = AuthAction((request, cookie) => {
    val response = createTopic(request, cookie, "草稿")
    Ok(Parser.ObjectToJsonString(response))
  })

  private[this] def createTopic(request: Request[AnyContent], cookie: Map[String, String], status: String) = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    val body = request.body.asMultipartFormData
    body match {
      case Some(form) => {
        val dataPart = form.dataParts.map { kv =>
          val key = kv._1
          val value = kv._2 match {
            case item :: list => {
              key match {
                case "expired" | "launchtime" => {
                  try {
                    println(key + "->" + item)
                    dateFormat.parse(item).getTime.toString
                  } catch {
                    case ex: Throwable =>
                      ex.printStackTrace()
                      System.currentTimeMillis().toString
                  }
                }
                case key_data => {
                  item
                }
              }
            }
            case _ => "default"
          }
          (key -> value)
        }.filter(_._1 != "new_topic_img")

        def checkPromotedTimeValid(map:Map[String,String])={
          val _status=map.getOrElse("topicstatus","default")
          if(_status=="发布"||status=="发布"){
            val launchtime=map.getOrElse("launchtime","-1")
            val expired=map.getOrElse("expired","-1")
            val position=map.getOrElse("lazytopicposition_name","default")
            val sql=s"select count(1) from lazytopic where launchtime<=${launchtime} and expired>=${expired} and lazytopicposition_name='${position}' and topicstatus='发布'"
            val count=SqlProvider.exec1(sql) match{
              case c::list=>c
              case _=> 0
            }
            if(count>0) false
            else true
          }else true
        }

        if(!checkPromotedTimeValid(dataPart)) throw new RuntimeException("发布失败,此时间端内已经存在相同的推广专题")

        val title = dataPart.getOrElse("title", "default1")
        val sql = s"select lazyadmin_id,topicstatus,img from lazytopic where title='${title}'"
        val lazyadmin = SqlProvider.exec(sql)
        def filePartsOperation() = {
          form.file("new_topic_img") match {
            case Some(filePart) => createFileHandler(filePart, cookie)
            case _ => ("", -1)
          }
        }
        val (newFileName, index) = filePartsOperation()
        def dataPartsOperation(titleImgId: String) = {
          lazyadmin match {
            case item :: list => {
              //update topic
              val lazystatus = item.getOrElse("topicstatus", "default3")
              if (lazystatus == "发布") {
                BadResponseContent("此专题已经发布,不允许修改")
              } else {
                val lazyadmin_id = item.getOrElse("lazyadmin_id", "default4")
                if (lazyadmin_id.toString.equals(cookie.getOrElse("uid", "default5"))) {
                  val action = {
                    dataPart.get("action")
                  }
                  val map: Map[String, String] = Map("img" -> titleImgId,
                    "lazyadmin_id" -> cookie.getOrElse("uid", "None"),
                    "modifytime" -> System.currentTimeMillis().toString,
                    "topictype" -> "default",
                    "topicposition" -> "default",
                    "topicstatus" -> status,
                    "location" -> "1",
                    "refto" -> "default"
                  ) ++ dataPart + ("action" -> ( dataPart.get("action").getOrElse("")))
                  try {
                    ResponseContent(AppTopicDao.updateTopic(map))
                  } catch {
                    case ex: Throwable => {
                      BadResponseContent(s"-1,${ex.getMessage}")
                    }
                  }
                } else {
                  BadResponseContent("没有权限修改其他管理员创建的专题")
                }
              }
            }
            case _ => {
              //create new topic
              val map: Map[String, String] = Map("img" -> titleImgId,
                "lazyadmin_id" -> cookie.getOrElse("uid", "None"),
                "creation" -> System.currentTimeMillis().toString,
                "topictype" -> "default",
                "topicposition" -> "default",
                "topicstatus" -> status,
                "location" -> "1",
                "refto" -> "default"
              ) ++ dataPart
              try {
                ResponseContent(AppTopicDao.addTopic(map))
              } catch {
                case ex: Throwable => {
                  BadResponseContent(s"-1,${ex.getMessage}")
                }
              }
            }
          }
        }
        val img = if (index.toString.toInt <= 0) {
          lazyadmin match {
            case item :: list => item.getOrElse("img", "default")
            case _ => "default"
          }
        } else newFileName
        if (status == "发布") {
          if (!img.equals("default")) {
            (dataPartsOperation(img.toString))
          } else {
            BadResponseContent("请上传专题位图片！")
          }
        } else {
          (dataPartsOperation(img.toString))
        }
      }
      case _ => BadResponseContent("上传的数据格式不符合要求！")
    }
  }

  val defaultImg = "/Users/yangguo/IdeaProjects/lazystoreproject/platform/lzbackup/data/dHJhaXQxNDMwMTE0NjIzMjk1.png"

  def getImgElement(id: String) = AuthAction { (request, _) =>
    val sql = s"select system_path from objmetadata where id='${id.trim}'"
    SqlProvider.exec2(sql) match {
      case item :: list => {
        println(item)
        Ok.sendFile(new java.io.File(item)).as("image/" + item.substring(item.lastIndexOf(".") + 1))
      }
      case _ => Ok.sendFile(new java.io.File(defaultImg)).as("image/" + defaultImg.substring(defaultImg.lastIndexOf(".") + 1))
    }
  }

  def getAppsTitleInfo = JsonpNoAuthAction { (request) =>
    val requestParams = request.queryString.map { kv =>
      val key = kv._1
      val value = kv._2.toList match {
        case item :: list => item
        case it =>
          println("->>>>>>" + it)
          "default"
      }
      (key -> value)
    }
    val columns = requestParams.getOrElse("columns", "lazyapp_id,title,icon,described,t_downloadcount,status,versioncode,pkgsize,totalstar,last_apppkg_id,downloadurls,packagename")
    val apps = requestParams.getOrElse("appIds", "default");
    val sql = s"select ${columns} from v_lazyapp_apppkg where lazyapp_id in (${apps}) order by find_in_set(lazyapp_id,'${apps}') "
    SqlProvider.exec(sql)
  }


  private[this] lazy val default_columns_appids = "title,id"

  def conditionSearchAppIds = JsonpAction { (request, cookie) =>
    val params = request.getQueryString("params") match {
      case Some(s) => s
      case _ => "default"
    }
    val columns = request.getQueryString("columns") match {
      case Some(s) => s
      case _ => default_columns_appids
    }
    val where = s"title like '%$params%'"
    val sql = s"select $columns from lazyapp where $where and status='release'"
    SqlProvider.exec(sql)
  }

  def test = Action { request =>
    val body = request.body.asMultipartFormData
    val response = body match {
      case Some(form) => {
        val dataPart = form.dataParts
        val files = form.files
        files.map { file =>
          val (filename, extensionName) = Util.getNameAndExtensionName(file.filename)
          val encoderFileName = Util.encoder(filename + System.currentTimeMillis())
          println(file.key + "->" + file.filename)
          0
        }.map(_.toString).reduce(_ + _)
      }
      case _ => -1
    }
    Ok(Parser.ObjectToJsonString(ResponseContent(response)))
  }

  def useIdsSearchInfo = JsonpAction { (request, map) =>
    val appids = request.getQueryString("ids") match {
      case Some(ids) => {
        val tids=ids.trim
        if(tids==""||tids.length<1) "-1"
        else tids
      }
      case None => "-1"
    }
    val columns = request.getQueryString("columns") match {
      case Some(cls) => cls
      case None => "lazyapp_id,title,icon,status"
    }
    val sql = s"select ${columns} from v_lazyapp_apppkg where lazyapp_id in (${appids}) order by find_in_set(lazyapp_id,'$appids')"
    SqlProvider.exec(sql)
  }
}
