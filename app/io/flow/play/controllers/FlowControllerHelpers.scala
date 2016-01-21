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

  /**
    * Helper class that responds to either form data or a Json
    * object. If we receive form data (content-type:
    * application/x-www-form-urlencoded), we turn that into a JSON
    * Object automatically. This enables us to provide API endpoints
    * that can respond either to form data or posted JSON objects. The
    * original use case for which we added support for form data was
    * to enable using "-d" from curl in the command line (simplifying
    * the examples for our users).
    */
  object JsValue {

    def async(
      contentType: Option[String],
      body: AnyContent
    ) (
      function: JsValue => Future[Result]
    ): Future[Result] = {
      parse(
        contentType,
        body,
        function,
        { errorResult => Future { errorResult } }
      )
    }

    def sync(
      contentType: Option[String],
      body: AnyContent
    ) (
      function: JsValue => Result
    ): Result = {
      parse(
        contentType,
        body,
        function,
        { errorResult => errorResult }
      )
    }

    private[this] def parse[T](
      contentType: Option[String],
      body: AnyContent,
      function: JsValue => T,
      errorFunction: Result => T
    ): T = {
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
          errorFunction(
            UnprocessableEntity(
              Json.toJson(
                Validation.error(s"Unsupported Content-Type, [$ct]. Must be 'application/x-www-form-urlencoded' or 'application/json'")
              )
            )
          )
        case None =>
          errorFunction(
            UnprocessableEntity(
              Json.toJson(
                Validation.error(s"Missing Content-Type Header. Must be 'application/x-www-form-urlencoded' or 'application/json'")
              )
            )
          )
      }
    }
  }

}
