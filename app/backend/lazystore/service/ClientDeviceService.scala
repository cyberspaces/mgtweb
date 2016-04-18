package backend.lazystore.service

import java.util.Date

import backend.lazystore.persistent.T.Tables.UAppstatsRow
import backend.lazystore.persistent.dao.ClientDeviceDao
import backend.base.util.{Parser, ResponseContent, RestRequest}

/**
 *  15-1-23.
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
