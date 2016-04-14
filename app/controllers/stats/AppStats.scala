package controllers.stats

import cn.changhong.lazystore.persistent.dao.SqlProvider
import cn.changhong.web.persistent.RedisPoolManager
import cn.changhong.web.util.{ ResponseContent, Parser}
import controllers.JsonpAction
import play.api.mvc.{ Controller}

/**
 *  16/2/23.
 * 增加两张表
 * 2.统计信息日表和统计信息月表
 *
 * 月表结构：time(已当月的时间戳作为值),appid,downloadcount,country
 */
class AppStats extends Controller{
  val tableName="tb_base_stats"
  val appName="lazystore"
  val redis_stats_current_user="f_online"
  val redis_stats_key="h_lazystore_base_stats"
  val tb_month_stats="tb_month_stats"
  val tb_day_stats="tb_day_stats"
//  //country,action(登陆,下载,退出,),time,clientId(暂定phonenumber),appName,clientVersion.remark,androidVersion
//  def addAppUsageStats()=Action{request=>
//    val responseContent=request.body.asText match{
//      case Some(jStr)=>ResponseContent(addNewUsageStatsRaw(Parser.JsonStringToMap(jStr)))
//      case None=>BadResponseContent("Message Format Error!")
//    }
//    Ok(Parser.ObjectToJsonString(responseContent))
//  }
  //url?st=10L,et=10L,max=20,page=1,[,country='china'][,type='day'/'month']
  //统计日表：time(天,时间戳),appID,downloadCount,country
  //统计月表：time(月,时间戳),appID,downloadCount,country
  def getAppUsageStats()=JsonpAction{(request,cookie)=>
    val st=request.getQueryString("st")
    val et=request.getQueryString("et")
    val page=request.getQueryString("page") match{
      case Some(_p)=>try{
        var pg=_p.toInt
        if(pg<0){
          pg=0
        }
        pg
      }catch{case ex:Throwable=>0}
      case None=>1
    }
    val max=request.getQueryString("max") match {
      case Some(_max) => try {
        _max.toInt
      } catch {
        case ex: Throwable => 20
      }
      case None => 20
    }
    val tableName=request.getQueryString("type") match{
      case Some(_type)=>
        if(_type.equalsIgnoreCase("day")) tb_day_stats
        else tb_month_stats
      case None=> tb_month_stats
    }
    val sql=request.getQueryString("country") match{
      case Some(country)=>s"select * from $tableName where country='$country' and time>=$st and time <=$et limit ${page*max},$max"
      case None=>s"select * from $tableName where time>=$st and time <=$et limit ${page*max},$max"
    }
    val result=SqlProvider.exec(sql)
    Ok(Parser.ObjectToJsonString(result))
  }

  def getBriefStats()=JsonpAction{(request,cookie)=>
    val value=RedisPoolManager.redisCommand{client=>
      client.hgetAll(redis_stats_key)
    }
    Ok(Parser.ObjectToJsonString(ResponseContent(value)))
  }
}
