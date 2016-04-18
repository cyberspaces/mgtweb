package cn.changhong.base.util

import java.util.concurrent.Executors

import cn.changhong.lazystore.GlobalConfig
import com.twitter.util.FuturePool

/**
 *  15-1-19.
 */
object ExecutorProvider {
  lazy val futurePool=FuturePool(Executors.newFixedThreadPool(GlobalConfig.executor_worker_max_thread_size))
}
