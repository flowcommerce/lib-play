package io.flow.play.clients

import play.api.libs.ws.WSClient

@javax.inject.Singleton
class DefaultUserClient @javax.inject.Inject() (
  registry: Registry,
  ws: WSClient
) extends io.flow.user.v0.Client(ws, registry.host("user"))

