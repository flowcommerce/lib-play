package io.flow.play.util

import play.api.libs.json.{JsObject, Json, JsValue}
import play.api.mvc.{Result, AnyContent}
import play.api.mvc.Results._
import scala.concurrent.Future


object FormDataHelper {
  def withJsValue(
   contentType: Option[String],
   body: AnyContent
  ) (
   function: JsValue => Future[Result]
  ): Future[Result] = {
    contentType match {
      case Some("application/x-www-form-urlencoded") =>
        function(
          body.asFormUrlEncoded.map(formDataToJson(_)).getOrElse(Json.obj())
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

  def convertFormDataValuesToJson(data: Map[String, Seq[String]]): Map[String, JsValue] = {
    data.map { case (key, value) =>
      key -> (value match {
        case a: Seq[Any] => {
          a.toList match {
            case one :: Nil => {
              // Normally we just have single parameters -
              // we default to NON array json value.
              Json.toJson(one)
            }
            case _ => {
              Json.toJson(a)
            }
          }
        }
        case _ => {
          Json.toJson(value)
        }
      })
    }
  }

  def formDataToJson(data: Map[String, Seq[String]]): JsValue = {
    val nested = convertFormDataValuesToJson(data).map(fd =>
      if(fd._1.contains(".")) {
        fd._1.split("\\.").foldRight(fd._2) { case (key, value) =>
          Json.obj(key -> value)}
      }
      else
        Json.obj(fd._1 -> fd._2)
    )

    Json.toJson(nested.foldLeft(Json.obj()){ case (a, b) => a.deepMerge(b.as[JsObject]) })
  }
}
