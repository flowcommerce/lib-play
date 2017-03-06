package io.flow.play.controllers

import io.flow.play.util.{Expander, FormData, Validation}
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc.{Result, AnyContent}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import play.api.libs.json._
import io.flow.error.v0.models.json._
import scala.concurrent.ExecutionContext.Implicits.global


trait FlowControllerHelpers {

  /**
    * Applications may override this variable to define applicable Expanders.
    * For example:
    * override expanders = Seq(new io.flow.play.expanders.User("user", userClient))
   */
  def expanders: Seq[Expander] = Nil
  private[this] lazy val expandersResult = {
    val tmp = expanders
    assert(tmp.nonEmpty, "You must have at least one expander when calling withExpansion")
    tmp
  }

  /**
   * Even if not specified, play router passed in Some(Nil) as opposed
   * to None. Here we return None if there is no list or the list is
   * empty.
   */
  def optionals[T](values: Option[Seq[T]]): Option[Seq[T]] = {
    values match {
      case None => None
      case Some(v) => {
        v match {
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
        { errorResult => Future(errorResult) }
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
            body.asFormUrlEncoded.map(FormData.formDataToJson).getOrElse(Json.obj())
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

  /**
    * Helper class that iterates through defined 'expanders' and attempts to expand Json objects of the expandable type.
    */

  object Expansion {
    def async(
     expand: Option[Seq[String]],
     records: Seq[JsValue],
     requestHeaders: Seq[(String, String)] = Nil
   ) (
     function: JsValue => Future[Result]
   ): Future[Result] = {
      withExpansion(
        expand,
        records,
        function,
        { errorResult => Future(errorResult) },
        requestHeaders = requestHeaders

      )
    }

    def sync(
      expand: Option[Seq[String]],
      records: Seq[JsValue],
      requestHeaders: Seq[(String, String)] = Nil
     ) (
       function: JsValue => Result
     ): Result = {
      withExpansion(
        expand,
        records,
        function,
        { errorResult => errorResult },
        requestHeaders = requestHeaders
      )
    }

    private[this] def withExpansion[T](
      expand: Option[Seq[String]],
      records: Seq[JsValue],
      function: JsValue => T,
      errorFunction: Result => T,
      requestHeaders: Seq[(String, String)] = Nil
    ): T = {
      val res = expandersResult.filter(e => expand.getOrElse(Nil).contains(e.fieldName)).foldLeft(records) {
        case (data, e) => Await.result(e.expand(data, requestHeaders = requestHeaders), Duration(5, "seconds"))
      }

      res match {
        case js: Seq[JsValue] => function(Json.toJson(js))
        case _ => errorFunction(
          UnprocessableEntity(
            Json.toJson(
              Validation.error(s"Expansion failed for 'expand': $expand and 'records': $records")
            )
          )
        )
      }
    }
  }
}
