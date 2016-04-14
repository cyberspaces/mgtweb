package cn.changhong.script

import scala.slick.codegen.SourceCodeGenerator

/**
 *  15-1-20.
 */
object SlickAutoGen {
  def main(args:Array[String]): Unit ={
    SourceCodeGenerator.main(Array(
      "scala.slick.driver.MySQLDriver",
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://10.9.52.31:13306/lazystore",
      "src/main/scala/",
      "controller.cn.changhong.lazystore.persistent.T",
      "appdev",
      "appdev"
    ))
  }
}
