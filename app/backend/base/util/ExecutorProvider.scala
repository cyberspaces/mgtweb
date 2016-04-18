package backend.base.util

import java.util.concurrent.Executors

import backend.lazystore.GlobalConfig
import com.twitter.util.FuturePool

/**
 *  15-1-19.
 */
object ExecutorProvider {
  lazy val futurePool=FuturePool(Executors.newFixedThreadPool(GlobalConfig.executor_worker_max_thread_size))
}
