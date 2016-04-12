package cn.changhong.lazystore.controller


import cn.changhong.web.router.RestAction
import cn.changhong.web.util.{ResponseContent, RestRequest}
import com.twitter.finagle.http.Request
import org.jboss.netty.handler.codec.http.HttpMethod

import scala.collection.mutable.Map

/**
 * Created by yangguo on 15-2-3.
 */
object RouterController {
  val r_start_url="^/lazystore/v1"

  val r_apps_page_search_url=r_start_url+"/apps\\.(\\w+)"
  val r_topic_url=s"$r_start_url/topic"
  val r_categories_url=s"$r_start_url/category"

  val r_comment_url=s"$r_start_url/comment"

  val r_comment_star_url=r_start_url+s"/comment\\.star"

  val r_device_url=s"$r_start_url/device"


  val r_stats_url=s"$r_start_url/stats"
  val r_app_info_url=s"$r_start_url/apps/(\\w+)"

  val r_user_apps_url=s"$r_start_url/u/apps"

  val routers: Map[(HttpMethod, String),RestAction[RestRequest,ResponseContent] ] = Map()

  //获取Apps
  routers+=((HttpMethod.GET->r_apps_page_search_url)->AppsQueryAction)

  //获取专题
  routers+=((HttpMethod.GET->r_topic_url)->TopicGetAction)

  //创建专题
  routers+=((HttpMethod.PUT->r_topic_url)->TopicPutAction)

  //获取app分类列表
  routers+=((HttpMethod.GET->r_categories_url)->CategoryGetAction)

  //获取app相关的用户评论
  routers+=((HttpMethod.GET->r_comment_url)->AppCommentGetAction)

  //获取用户对app的评论星级
  routers+=((HttpMethod.GET->r_comment_star_url)->AppCommentStarGetAction)

  //添加App评论
  routers+=((HttpMethod.PUT->r_comment_url)->AppCommentPutAction)

  //创建设备信息
  routers+=((HttpMethod.PUT->r_device_url)->DevicePutAction)

  //上传设备端app的统计信息
  routers+=((HttpMethod.PUT->r_stats_url)->StatsPutAction)

  //获取App详细信息
  routers+=((HttpMethod.GET->r_app_info_url)->AppGetAction)

  //上传设备端所有app
  routers+=((HttpMethod.PUT->r_user_apps_url)->DeviceAppsPutAction)

  def filterRouter(request:RestRequest)={
    routers.filter{router=>
      if(request.method == router._1._1 && request.path.matches(router._1._2)) true
      else false
    }
  }
}
