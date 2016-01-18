package io.flow.play.controllers

import io.flow.play.util.{FormData, Validation}
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc.{Result, AnyContent}
import scala.concurrent.Future
import play.api.libs.json._
import io.flow.common.v0.models.json._
import scala.concurrent.ExecutionContext.Implicits.global


trait FlowControllerHelpers {

  /**
   * Even if not specified, play router passed in Some(Nil) as opposed
   * to None. Here we return None if there is no list or the list is
   * empty.
   */
  def optionals[T](guids: Option[Seq[T]]): Option[Seq[T]] = {
    guids match {
      case None => None
      case Some(values) => {
        values match {
          case Nil => None
          case ar => Some(ar)
        }
      }
    }
  }

  def withJsValue(
   contentType: Option[String],
   body: AnyContent
  ) (
   function: JsValue => Future[Result]
  ): Future[Result] = {
    contentType match {
      case Some("application/x-www-form-urlencoded") =>
        function(
          body.asFormUrlEncoded.map(FormData.formDataToJson(_)).getOrElse(Json.obj())
        )
      case Some("application/json") =>
        function(
          body.asJson match {
            case Some(json) => json
            case None => Json.toJson(Validation.invalidJsonDocument())
          }
        )
      case Some(ct) =>
        Future {
          UnprocessableEntity(
            Json.toJson(
              Validation.error(s"Unsupported Content-Type, [$ct]. Must be 'application/x-www-form-urlencoded' or 'application/json'")
            )
          )
        }
      case None =>
        Future {
          UnprocessableEntity(
            Json.toJson(
              Validation.error(s"Missing Content-Type Header. Must be 'application/x-www-form-urlencoded' or 'application/json'")
            )
          )
        }
    }
  }
}
