package backend.lazystore.controller

import backend.lazystore.persistent.T.Tables.UAppstatsRow
import backend.lazystore.service.ClientDeviceService
import backend.base.router.RestAction
import backend.base.util.{RestRespCode, RestException, ResponseContent, RestRequest}
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 *  15-2-4.
 */


object StatsPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent =ClientDeviceService.AddClientDeviceCopStats(request)
}