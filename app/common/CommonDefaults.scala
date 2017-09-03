package common

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import play.api.mvc.PlayBodyParsers

trait CommonDefaults {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  val defaultBodyParser = PlayBodyParsers().default
}
