package controllers

import cn.changhong.lazystore.persistent.dao.SqlProvider
import play.api.mvc._

/**
 *  15-4-15.
 */
object AppsApplication extends Controller{
  def addNewApp=AuthAction{(request,_)=>
    Ok("")
  }

  def getStatsPosition = JsonpNoAuthAction { (request) =>
    val sql = "SELECT position label, COUNT(*)  value FROM u_device dev GROUP BY dev.position"
    SqlProvider.exec(sql)
  }

  def getStatsProvider = JsonpNoAuthAction { (request) =>
    val sql = "SELECT providername label, COUNT(*)  value FROM u_device dev GROUP BY dev.providername"
    SqlProvider.exec(sql)
  }

  def getStatsProducer = JsonpNoAuthAction { (request) =>
    val sql = "SELECT producer label, COUNT(*)  value FROM u_device dev GROUP BY dev.producer"
    SqlProvider.exec(sql)
  }

  def getStatsAndroidsystem = JsonpNoAuthAction { (request) =>
    val sql = "SELECT system label, COUNT(*)  value FROM u_device dev GROUP BY dev.system"
    SqlProvider.exec(sql)
  }

  def getStatsAppsUseFrequency = JsonpNoAuthAction { (request) =>
    val sql = "SELECT sts.packagename, sum(sts.frequency) rsvalue\nFROM u_appstats sts\nwhere sts.statsdate>UNIX_TIMESTAMP('2015-12-01 00:00:00')*1000\nGROUP BY sts.packagename\nHAVING rsvalue>0\nORDER BY rsvalue desc\nLIMIT 10"
    SqlProvider.exec(sql)
  }

  def getStatsAppsBattery = JsonpNoAuthAction { (request) =>
    val sql = "SELECT sts.packagename, AVG(sts.battery) rsvalue\nFROM u_appstats sts\nwhere sts.statsdate>UNIX_TIMESTAMP('2015-12-01 00:00:00')*1000\nGROUP BY sts.packagename\nHAVING rsvalue>0\nORDER BY rsvalue desc\nLIMIT 10"
    SqlProvider.exec(sql)
  }

  def getStatsAppsTraffic = JsonpNoAuthAction { (request) =>
    val sql = "SELECT sts.packagename, sum(sts.traffic) rsvalue\nFROM u_appstats sts\nwhere sts.statsdate>UNIX_TIMESTAMP('2015-12-01 00:00:00')*1000\nGROUP BY sts.packagename\nHAVING rsvalue>0\nORDER BY rsvalue desc\nLIMIT 10"
    SqlProvider.exec(sql)
  }

  def getStatsAppsMemory = JsonpNoAuthAction { (request) =>
    val sql = "SELECT sts.packagename, AVG(sts.mem) rsvalue\nFROM u_appstats sts\nwhere sts.statsdate>UNIX_TIMESTAMP('2015-12-01 00:00:00')*1000\nGROUP BY sts.packagename\nHAVING rsvalue>0\nORDER BY rsvalue desc\nLIMIT 10"
    SqlProvider.exec(sql)
  }

}
