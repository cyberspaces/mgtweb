package cn.changhong.lazystore.service

import java.util.Date

import cn.changhong.lazystore.persistent.T.Tables.UAppstatsRow
import cn.changhong.lazystore.persistent.dao.ClientDeviceDao
import cn.changhong.web.util.{Parser, ResponseContent, RestRequest}

/**
 * Created by yangguo on 15-1-23.
 */
object ClientDeviceService {
  object AddClientDeviceService extends AddClientDeviceService with BaseAopService
  private[service] class AddClientDeviceService extends BaseService{
    override def apply(request: RestRequest): ResponseContent = {
      val content=ClientDeviceDao.addClientDevice(request)
      ResponseContent(content)
    }
  }

  object AddClientDeviceCopStats extends AddClientDeviceCopStats with BaseAopService
  private[service] class AddClientDeviceCopStats extends BaseService{
    override def apply(request: RestRequest): ResponseContent = {
      val content=ClientDeviceDao.addClientDeviceCopStats(request)
      ResponseContent(content)
    }
  }

  object AddClientDeviceAppsService extends AddClientDeviceAppsService with BaseAopService
  private[service] class AddClientDeviceAppsService extends BaseService{
    override def apply(request: RestRequest): ResponseContent = {
      val deviceApps = Parser.DeviceAppsParser(Parser.ChannelBufferToString(request.underlying.getContent))
      val now = new Date().getTime()
      val uApps = deviceApps.uApps.map { uapp =>
        uapp.uDeviceId = deviceApps.uDeviceId
        uapp.updatetime = now
        uapp.id = -1
        uapp
      }
      val content = ClientDeviceDao.addClientDeviceApps(uApps)
      ResponseContent(content)
    }
  }
}
