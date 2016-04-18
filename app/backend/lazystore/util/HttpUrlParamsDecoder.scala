package cn.changhong.lazystore.util

/**
 *  15-2-11.
 */
object HttpUrlParamsDecoder {
  def decode(uri:String)={
    uri.split("?")
  }
}
