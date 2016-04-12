package cn.changhong.web.util

import java.nio.charset.Charset

import com.twitter.finagle.http.Response
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}
import org.jboss.netty.handler.codec.http.{HttpVersion, HttpResponseStatus}
import org.jboss.netty.util.CharsetUtil

/**
 * Created by yangguo on 14-12-9.
 */
//case class RestResponseContent(code:Int,jsonObj:Any)

object DefaultHttpResponse {
  /**
   *
   * @param contentObj http body 信息
   * @param header http 头部信息
   * @return
   */
  def createResponse(contentObj: RestResponseContent, header: Option[Map[String, String]]=None, charset: Charset = CharsetUtil.UTF_8): Response = {
    val contentBuffer = Parser.StringToChannelBuffer(Parser.ObjectToJsonString(contentObj))
    val response = Response()
    response.setContent(contentBuffer)
    if (header.isDefined) header.get foreach { kv => response.headers.set(kv._1, kv._2)}
    response
  }
}
