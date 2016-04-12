package cn.changhong.lazystore.util

/**
 * Created by yangguo on 15-2-11.
 */
object HttpUrlParamsDecoder {
  def decode(uri:String)={
    uri.split("?")
  }
}
