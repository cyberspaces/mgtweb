package cn.changhong.lazystore.controller

import cn.changhong.lazystore.service.ClientDeviceService
import cn.changhong.base.router.RestAction
import cn.changhong.base.util.{RestRespCode, RestException, ResponseContent, RestRequest}
import org.jboss.netty.handler.codec.http.HttpMethod

/**
 *  15-2-4.
 */

/**
 * 设备相关
 */
object DevicePutAction extends RestAction[RestRequest,ResponseContent] {
  override def apply(request: RestRequest): ResponseContent = ClientDeviceService.AddClientDeviceService(request)

}
object DeviceAppsPutAction extends RestAction[RestRequest,ResponseContent]{
  override def apply(request: RestRequest): ResponseContent =ClientDeviceService.AddClientDeviceAppsService(request)
}