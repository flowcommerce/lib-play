package common

import play.api.libs.ws.ahc.{AhcWSClient, StandaloneAhcWSClient}
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient

trait WsStandaloneClient extends CommonDefaults {
  val wsClient = new AhcWSClient(new StandaloneAhcWSClient(new DefaultAsyncHttpClient))
}
