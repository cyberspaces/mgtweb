package cn.changhong.web.persistent

import java.util.UUID

import cn.changhong.web.init.GlobalConfigFactory
import redis.clients.jedis.{JedisPool, JedisPoolConfig,Jedis}

/**
 * Created by yangguo on 14-12-10.
 */
object RedisPoolManager{
  private[this] val redisPool={
    val config=new JedisPoolConfig
    config.setMaxTotal(500)
    config.setMaxIdle(5)
    config.setMaxWaitMillis(1000*10)
    config.setTestOnBorrow(true)
    new JedisPool(config,GlobalConfigFactory.redis_server_ip,GlobalConfigFactory.redis_server_port)
}
  def redisCommand[T](f:Jedis=>T): T ={
    val client=redisPool.getResource
    try{
      f(client)
    }finally {
      redisPool.returnBrokenResource(client)
    }
  }
}
object Test{
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