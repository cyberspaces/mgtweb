package cn.changhong.web.util

import java.util.concurrent.{TimeUnit, SynchronousQueue, ThreadPoolExecutor, Executors}

import cn.changhong.web.init.GlobalConfigFactory
import com.twitter.util.FuturePool

/**
 * Created by yangguo on 14-12-12.
 */
object ActionExecutorProvider {
 lazy val futurePool=FuturePool(Executors.newFixedThreadPool(4))
}
