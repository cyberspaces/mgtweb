package backend.lazystore.controller

import backend.lazystore.service.ClientDeviceService
import backend.base.router.RestAction
import backend.base.util.{RestRespCode, RestException, ResponseContent, RestRequest}
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