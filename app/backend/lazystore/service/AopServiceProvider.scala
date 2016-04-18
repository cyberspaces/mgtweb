package backend.lazystore.service

import backend.lazystore.GlobalConfig
import backend.base.util._
import org.slf4j.LoggerFactory

/**
 *  15-1-19.
 */
trait BaseService{
  def apply(request:RestRequest):ResponseContent
}
trait LogService extends BaseService{
  val log=LoggerFactory.getLogger(GlobalConfig.log_user_name)
  abstract override def apply(request:RestRequest):ResponseContent={
    log.info(createLog(request))
    super.apply(request)
  }
  def createLog(request:RestRequest): String ={
    Parser.ObjectToJsonString(request.logBean)
  }
}
trait TempAuthCheckService extends BaseService{
  abstract override def apply(request:RestRequest):ResponseContent={
    if(!checkTempAuth(request)) throw new RestException(RestRespCode.permission_need,"permission invalid")
    super.apply(request)
  }
  def checkTempAuth(request:RestRequest):Boolean={
    true
  }
}
trait BaseAopService extends TempAuthCheckService with LogService