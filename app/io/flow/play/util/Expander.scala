package io.flow.play.util

import play.api.libs.json._
import scala.concurrent.{Future, ExecutionContext}


trait Expander {
  def expand(records: Seq[JsValue])(implicit ec: ExecutionContext): Future[Seq[JsValue]]
}
