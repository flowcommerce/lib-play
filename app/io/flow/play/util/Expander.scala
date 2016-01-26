package io.flow.play.util

import play.api.libs.json._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global


trait Expander {
  val fieldName: String
  def expand(records: Seq[JsValue])(implicit ec: ExecutionContext): Future[Seq[JsValue]]
}

object Expander {
  def expandedJson(expanders: Seq[Expander], expand: Option[Seq[String]], records: Seq[JsValue]) = {
    Json.toJson(expanders.filter(e => expand.getOrElse(Nil).contains(e.fieldName)).foldLeft(records) {
      case (records, e) => Await.result(e.expand(records), Duration(5, "seconds"))
    })
  }
}
