package controllers

/**
 * Created by yangguo on 15-4-7.
 */
object GlobalConfiguration {
  val Request_Header_Auth_Key="Authorization"

  val Request_Header_Range="Range" //Range:start-end

  val Response_Header_ContentRange="Content-Range"//Content-Range:start-end/total

  val Response_Header_Server_Version="Server Version"
  val Response_Header_Server_Name="Server Name"
  val Response_Header_ContentType="Content-type"


  var Server_Name:Option[String]=None
  var Server_Version:Option[String]=None

}
