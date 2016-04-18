package backend.lazystore.persistent.script

import scala.slick.codegen.SourceCodeGenerator

/**
 *  14-12-10.
 */
object SlickAutoGen {
  def mainTest(args:Array[String]): Unit ={
    SourceCodeGenerator.main(Array(
      "scala.slick.driver.MySQLDriver",
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost:3306/lazystore",
      "src/main/scala/",
      "cn.changhong.web.persistent.Tables",
      "yangguo",
      "123456"
    ))
  }
}
