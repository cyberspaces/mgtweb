package backend.lazystore.service

import backend.lazystore.persistent.dao.UserCommentDao
import backend.base.util._

/**
 *  15-1-22.
 */


  object AddAppCommentService extends AddAppCommentService with BaseAopService

  private[service] class AddAppCommentService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val comment = Parser.UAppcommentsParser(Parser.ChannelBufferToString(request.underlying.getContent))
      val content = UserCommentDao.createNewComment(comment)
      ResponseContent(content)
    }
  }

  /**
   * 返回app评论的星级
   */
  object GetAppCommentStarService extends GetAppCommentStarService with BaseAopService

  private[service] class GetAppCommentStarService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = AppsRequest(request).condition match {
        case Some(s) => UserCommentDao.getAppCommentStatsStar(s)
        case None =>throw new RestException(RestRespCode.invalid_request_parameters,"Need App Id")
      }
      ResponseContent(content)
    }
  }

  object GetAppCommentService extends GetAppCommentService with BaseAopService

  private[service] class GetAppCommentService extends BaseService {
    override def apply(request: RestRequest): ResponseContent = {
      val content = UserCommentDao.getAppComment(AppsRequest(request))
      ResponseContent(content)
    }
  }


