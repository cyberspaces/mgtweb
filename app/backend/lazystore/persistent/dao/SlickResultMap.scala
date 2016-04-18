package cn.changhong.lazystore.persistent.dao

import scala.slick.jdbc.{PositionedResult, GetResult}

/**
 *  15-1-21.
 */
object SlickResultString extends GetResult[String]{
  override def apply(pr: PositionedResult): String = {
    pr.rs.getObject(1).toString
  }
}
object SlickResultInt extends GetResult[Int]{
  override def apply(pr: PositionedResult): Int = {
    pr.rs.getObject(1).toString.toInt
  }
}
object SlickResultMap extends GetResult[Map[String,Any]]{
  override def apply(pr: PositionedResult): Map[String, Any] = {
    val cvalues=pr.rs
    val cnames=cvalues.getMetaData
    val res = (1 to pr.numColumns).map{ i=>
      val obj=cvalues.getObject(i)
      val alias=cnames.getColumnName(i) match{
        case "speitysort" | "topsort" | "hotsort" | "othersort" =>"sid"
        case s=>s
      }
      (alias -> {
        if(obj.isInstanceOf[java.math.BigInteger]) try{obj.toString.toLong}catch{case ex:Throwable=> -1}
        else obj
      })
    }
    res.toMap
  }
}

