/**
  * Created by Administrator on 2016/3/25.
  */
class RedisSpec{
  def main(args:Array[String]): Unit ={
    val max_valid_request_frequency=10
    val max_valid_request_expire_seconds=10
    RedisPoolManager.redisCommand{implicit client=>
      val key="h_type_uid_cid"
      val count=client.incr(key)
      if(count>max_valid_request_frequency) true
      else {
        if(count == 1) client.expire(key,max_valid_request_expire_seconds)
        false
      }
      println(client.set("key1","value1"))
      println(client.set("key1","value1"))
    }
  }
}


