package io.flow.play.bodyparsers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{BodyParser, PlayBodyParsers, Results}
import play.api.libs.json._
import scala.concurrent.ExecutionContext

@Singleton
class JsonReadsBodyParsers @Inject()(bodyParsers: PlayBodyParsers,
                                     results: Results) {

  def tolerantJsonReads[A: Reads](
      implicit ec: ExecutionContext): BodyParser[A] =
    jsonReads(bodyParsers.tolerantJson)

  def jsonReads[A: Reads](implicit ec: ExecutionContext): BodyParser[A] =
    jsonReads(bodyParsers.json)

  def jsonReads[A: Reads](bodyParser: BodyParser[JsValue])(
      implicit ec: ExecutionContext): BodyParser[A] =
    bodyParser.validate { jsValue =>
      jsValue
        .validate[A]
        .asEither
        .left.map(e => results.BadRequest(JsError.toJson(e)))
    }

}
