package controllers.bootstrap

import java.util.concurrent.atomic.AtomicInteger

import cn.changhong.web.util.Parser
import play.api.mvc._

/**
 * Created by yangguo on 15-4-21.
 */
case class Product(id:Long,price:Double,name:String)
object TestApplication extends Controller{
  val count=new AtomicInteger(1)
  def getData()=Action{request=>
    println(request.path)
    request.queryString.foreach(println(_))
    val res=(1 to 10).map{index=>Product(index,index/10.0,"name"+index)}
    Ok(Parser.ObjectToJsonString(Map("rows"->res,"total"->100,"start"->count.getAndIncrement)))
  }
}
