package backend.lazystore.util

import com.google.gson.GsonBuilder

/**
 *  15-8-7.
 */
object JavaObjToJsonString {
  private lazy val gsonDefaultBuilder=new GsonBuilder().create()
  def apply(obj:AnyRef)=gsonDefaultBuilder.toJson(obj)
}
