package io.flow.play.util

import play.api.libs.json._
import play.api.mvc.Request
import scala.concurrent.{Future, ExecutionContext}


trait Expander {
  val fieldName: String
  def expand(records: Seq[JsValue])(implicit ec: ExecutionContext, request: Request[_]): Future[Seq[JsValue]]
}
