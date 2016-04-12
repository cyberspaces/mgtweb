package cn.changhong.web.util

import java.util.concurrent.Executors

import cn.changhong.web.init.GlobalConfigFactory
import com.twitter.util.FuturePool

/**
 * Created by yangguo on 15-1-19.
 */
object ExecutorProvider {
  lazy val futurePool=FuturePool(Executors.newFixedThreadPool(GlobalConfigFactory.executor_worker_max_thread_size))
}
