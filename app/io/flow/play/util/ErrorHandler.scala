package io.flow.play.util

import io.flow.error.v0.models.json._
import io.flow.log.RollbarLogger
import javax.inject.Inject
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results.{InternalServerError, Status}

import scala.concurrent.Future

/**
  * Custom error handler that always returns application/json
  * 
  * Server errors are logged w/ a unique error number that is presented
  * in the message back to the client. This allows us to quickly cross
  * reference an error to a specific point in the log.
  */
class ErrorHandler @Inject() (logger: RollbarLogger) extends HttpErrorHandler {

  private[this] val idGenerator = IdGenerator("err")

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    statusCode match {
      case 401 | 404 => Future.successful(Status(statusCode))
      case _ => {
        message.trim match {
          case "" => Future.successful(Status(statusCode))
          case msg => Future.successful(Status(statusCode)(Json.toJson(Validation.error(msg))))
        }
      }
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val headerMap = request.headers.toMap
    val requestId = headerMap.getOrElse("X-Flow-Request-Id", Nil).mkString(",")

    val errorId = idGenerator.randomId().replaceAll("-", "")
    logger.
      withKeyValue("error_id", errorId).
      withKeyValue("request_method", request.method).
      withKeyValue("request_path", request.path).
      withKeyValue("request_id", requestId).
      error("server error", exception)

    val msg = FlowEnvironment.Current match {
      case FlowEnvironment.Development | FlowEnvironment.Workstation => s"A server error has occurred (#$errorId) for requestId($requestId). Additional info for development environment: $exception"
      case FlowEnvironment.Production => s"A server error has occurred (#$errorId)"
    }

    Future.successful(InternalServerError(Json.toJson(Validation.error(msg))))
  }
}
