package cn.changhong.web.persistent.script

import scala.slick.codegen.SourceCodeGenerator

/**
 * Created by yangguo on 14-12-10.
 */
object SlickAutoGen {
  def main(args:Array[String]): Unit ={
    SourceCodeGenerator.main(Array(
      "scala.slick.driver.MySQLDriver",
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost:3306/crazycat",
      "src/main/scala/",
      "cn.changhong.web.persistent.Tables",
      "yangguo",
      "123456"
    ))
  }
}
