package backend.lazystore.util

import java.util.UUID

/**
 * Created by Administrator on 2015/2/2.
 */
object KeyGenerator {
  def createUUID={
    UUID.randomUUID().toString
  }
}
