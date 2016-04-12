package controllers.cn.changhong.lazystore.controller.Bean

/**
 * Created by yangguo on 15-4-15.
 */
trait TopicTemplate
object TopicType{
  val Topic_Template_1="topic_template_1"
}

case class TopicTemplate1(url:String,appIds:Seq[String],keywords:String) extends TopicTemplate

case class TopicAction(topicType:String,topicTemplate: TopicTemplate)


