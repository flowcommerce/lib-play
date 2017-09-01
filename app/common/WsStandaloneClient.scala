package common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient

trait WsStandaloneClient {
  import play.api.libs.ws.ahc.StandaloneAhcWSClient
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val wsClient = new AhcWSClient(new StandaloneAhcWSClient(new DefaultAsyncHttpClient))
}
