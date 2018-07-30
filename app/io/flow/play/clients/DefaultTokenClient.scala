package io.flow.play.clients

import io.flow.util.clients.RegistryConstants
import play.api.libs.ws.WSClient

/**
  * Gets an instance of the token client. Note that we cannot declare
  * a dependency on registry here as registry requires token to
  * authorize the user. Thus we create an instance of the token client
  * but explicitly set the port to known values for both development
  * and production.
  */
@javax.inject.Singleton
class DefaultTokenClient @javax.inject.Inject() (ws: WSClient) extends io.flow.token.v0.Client(ws,
  RegistryConstants.host("token", 6151)
)
