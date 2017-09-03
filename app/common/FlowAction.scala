package common

import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

 abstract class FlowAction[R[_]](implicit ec: ExecutionContext) extends  ActionBuilder[R, AnyContent] with BodyParsers with CommonDefaults {
  override def parser = defaultBodyParser
   override protected def executionContext = ec
 }
