package cn.changhong.base.util

/**
 *  14-12-9.
 */
case class RestResponseContent(code:Int,jsonObj:Any)
case class ResponseContent(code:Int,jsonObj:Any)
case class PagingResponseContent(code:Int,jsonObj:Any,total:Int)

object ResponseContent{
  def apply(jsonObj:Any):ResponseContent={
    ResponseContent(RestRespCode.succeed,jsonObj)
  }
}

object PagingResponseContent{
  def apply(jsonObj:Any,total:Int):PagingResponseContent=PagingResponseContent(RestRespCode.succeed,jsonObj,total)
}

object BadResponseContent{
  def apply(ex:Throwable)={
    ResponseContent(RestRespCode.bad_request,ex.getMessage)
  }
  def apply(msg:String)= {
    ResponseContent(RestRespCode.bad_request,msg )

  }
}

