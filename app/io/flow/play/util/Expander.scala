package io.flow.play.util

import play.api.libs.json._
import scala.concurrent.{Future, ExecutionContext}


trait Expander {
  val fieldName: String
  def expand(records: Seq[JsValue])(implicit ec: ExecutionContext): Future[Seq[JsValue]]
}
