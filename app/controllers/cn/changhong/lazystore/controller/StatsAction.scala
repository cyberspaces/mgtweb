package cn.changhong.lazystore.controller

import cn.changhong.lazystore.persistent.T.Tables.UAppstatsRow
import cn.changhong.lazystore.service.ClientDeviceService
import cn.changhong.web.router.RestAction
import cn.changhong.web.util.{RestResponseInlineCode, RestException, ResponseContent, RestRequest}
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 * Created by yangguo on 15-2-4.
 */


object StatsPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent =ClientDeviceService.AddClientDeviceCopStats(request)
}