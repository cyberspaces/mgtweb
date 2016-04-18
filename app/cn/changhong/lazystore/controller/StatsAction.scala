package cn.changhong.lazystore.controller

import cn.changhong.lazystore.persistent.T.Tables.UAppstatsRow
import cn.changhong.lazystore.service.ClientDeviceService
import cn.changhong.base.router.RestAction
import cn.changhong.base.util.{RestRespCode, RestException, ResponseContent, RestRequest}
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 *  15-2-4.
 */


object StatsPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent =ClientDeviceService.AddClientDeviceCopStats(request)
}