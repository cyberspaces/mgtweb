package cn.changhong.web.init

import java.util.Properties
import java.util.concurrent.TimeUnit

import com.twitter.util.Duration

/**
 * Created by yangguo on 14-12-10.
 */

object GlobalConfigFactory {
  var properties:Properties=null
  var db_driver=""
  var db_username=""
  var db_password=""
  var db_url=""
  var db_maxActive=255
  var db_maxIdle=2
  var db_maxWait=1200
  var db_thread_init_size=5
  var executor_worker_max_thread_size=4

  var global_response_timeout=Duration(5,TimeUnit.SECONDS)

  var global_log_request_access_name="logInfo"
  val global_log_request_error_name="logError"
  val global_log_request_tracker_name="logTracker"
  val global_log_request_spider_name="logSpider"

  var server_ip="localhost"
  var server_port=10001
  var server_id="ch_account_server1"
  var server_name="lazystore_f01"


  val max_valid_request_frequency=20
  val max_valid_request_expire_seconds=10
  val exceed_spider_threshold_frequency=100
  val exceed_spider_threshold_seconds=3600
  val may_spider_sleep_seconds=10

  /*分页查询默认返回的数据条数*/
  val default_apps_count=24

  val log_user_name=""


  var redis_server_ip ="localhost"
  var redis_server_port:Int=6379

  def apply(confPath:String): Unit ={
    val confInputStream=this.getClass.getClassLoader.getResourceAsStream("webconf.properties")
    val p=new Properties()//(confInputStream)
    p.load(confInputStream)
    this.properties = p
  }
}
