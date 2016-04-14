package cn.changhong.web.util

/**
 *  14-12-9.
 */
case class RestResponseContent(code:Int,jsonObj:Any)
case class ResponseContent(code:Int,jsonObj:Any)
case class PagingResponseContent(code:Int,jsonObj:Any,total:Int)

object ResponseContent{
  def apply(jsonObj:Any):ResponseContent={
    ResponseContent(RestResponseInlineCode.succeed,jsonObj)
  }
}

object PagingResponseContent{
  def apply(jsonObj:Any,total:Int):PagingResponseContent=PagingResponseContent(RestResponseInlineCode.succeed,jsonObj,total)
}

object BadResponseContent{
  def apply(ex:Throwable)={
    ResponseContent(RestResponseInlineCode.bad_request,ex.getMessage)
  }
  def apply(msg:String)= {
    ResponseContent(RestResponseInlineCode.bad_request,msg )

  }
}

