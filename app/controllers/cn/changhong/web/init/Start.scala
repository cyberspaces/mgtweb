package cn.changhong.web.init

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import cn.changhong.web.router._
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Request, RichHttp, Http}
import com.twitter.util.Duration

/**
 * Created by yangguo on 14-12-8.
 */
object Start {
  def main(args:Array[String]): Unit ={
    GlobalConfigFactory.server_ip=args(0)
    GlobalConfigFactory.server_port=args(1).toInt
    val service = AccessLogFilterService andThen ExceptionFilterService andThen SpiderActionInspectorFilterService andThen  TimeoutFilterService andThen ForeRouter
    ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .readTimeout(Duration(5,TimeUnit.SECONDS))
      .bindTo(new InetSocketAddress(GlobalConfigFactory.server_ip,GlobalConfigFactory.server_port))
      .name(args(2))
      .build(service)
  }
}
