package backend.lazystore.persistent

import org.apache.commons.dbcp.BasicDataSource

import scala.slick.driver.MySQLDriver.simple._
/**
 *  14-12-10.
 */

object SlickDBPoolManager {
  val DBPool={
    val ds=new BasicDataSource
    ds.setDriverClassName("com.mysql.jdbc.Driver")
    ds.setUsername("appdev")
    ds.setPassword("appdev78123!@#") //appdev78
    ds.setMaxActive(20)
    ds.setMaxIdle(10)
    ds.setInitialSize(5)
    ds.setTestOnBorrow(true)
    ds.setRemoveAbandoned(true)
    ds.setRemoveAbandonedTimeout(300) //300s
    ds.setLogAbandoned(true)
    // localhost autoReconnect=true
    ds.setUrl("jdbc:mysql://us.newasst.com:3306/lazystore?characterEncoding=UTF-8&autoReconnect=true")
    ds.setMaxWait(1)
    Database.forDataSource(ds)
  }
}
