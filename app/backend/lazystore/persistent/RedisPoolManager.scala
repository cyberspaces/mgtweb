package backend.lazystore.persistent

import backend.lazystore.GlobalConfig
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 *  14-12-10.
 */
object RedisPoolManager{
  private[this] val redisPool={
    val config=new JedisPoolConfig
    config.setMaxTotal(500)
    config.setMaxIdle(5)
    config.setMaxWaitMillis(1000*10)
    config.setTestOnBorrow(true)
    new JedisPool(config, GlobalConfig.redis_server_ip, GlobalConfig.redis_server_port, 3000, "Kksebo", 0)
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
