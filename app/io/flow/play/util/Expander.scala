package io.flow.play.util

import play.api.libs.json._
import scala.concurrent.{Future, ExecutionContext}

trait Expander {
  val fieldName: String
  def expand(records: Seq[JsValue], requestHeaders: Seq[(String, String)] = Nil)(implicit
    ec: ExecutionContext,
  ): Future[Seq[JsValue]]
}
