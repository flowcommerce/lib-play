package io.flow.play.util

import io.flow.common.v0.models.json._
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.Logger
import play.api.mvc.RequestHeader
import play.api.mvc.Results.{InternalServerError, Status}
import scala.concurrent.Future

/**
  * Custom error handler that always returns application/json
  * 
  * Server errors are logged w/ a unique error number that is presented
  * in the message back to the client. This allows us to quickly cross
  * reference an error to a specific point in the log.
  */
class ErrorHandler extends HttpErrorHandler {

  private[this] val idGenerator = IdGenerator("err")

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    statusCode match {
      case 401 | 404 => Future.successful(Status(statusCode))
      case other => {
        message.trim match {
          case "" => Future.successful(Status(statusCode))
          case msg => Future.successful(Status(statusCode)(Json.toJson(Validation.clientError(msg))))
        }
      }
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable) = {
    val errorId = idGenerator.randomId()
    Logger.error(s"Error[$errorId] ${request.method} ${request.path}", exception)

    val msg = FlowEnvironment.Current match {
      case FlowEnvironment.Development => s"A server error has occurred (#$errorId). Additional info for development environment: $exception"
      case FlowEnvironment.Production => s"A server error has occurred (#$errorId)"
    }

    Future.successful(InternalServerError(Json.toJson(Validation.serverError(msg))))
  }
}
