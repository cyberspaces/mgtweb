package cn.changhong.lazystore

import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.TimeUnit

import cn.changhong.lazystore.controller.LazyStoreForeRouter
import cn.changhong.web.init.GlobalConfigFactory
import cn.changhong.web.router.{TimeoutFilterService, SpiderActionInspectorFilterService, ExceptionFilterService, AccessLogFilterService}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, RichHttp}
import com.twitter.util.Duration



import scala.concurrent.future

/**
 * Created by Administrator on 2015/2/2.
 */
object Start {
  def main(args:Array[String]): Unit ={
    GlobalConfigFactory.server_ip=args(0)
    GlobalConfigFactory.server_port=args(1).toInt
    GlobalConfigFactory.server_name=args(2)
    GlobalConfigFactory.redis_server_ip=args(3)
    GlobalConfigFactory.redis_server_port=args(4).toInt
    val service = AccessLogFilterService andThen ExceptionFilterService andThen SpiderActionInspectorFilterService andThen  TimeoutFilterService andThen LazyStoreForeRouter
    ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .readTimeout(Duration(5,TimeUnit.SECONDS))
      .bindTo(new InetSocketAddress(GlobalConfigFactory.server_port))
      .name(GlobalConfigFactory.server_name)
      .build(service)
  }
}