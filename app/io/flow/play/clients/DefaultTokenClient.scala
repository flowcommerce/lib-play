package io.flow.play.clients

@javax.inject.Singleton
class DefaultTokenClient @javax.inject.Inject() (
  registry: Registry
) extends io.flow.token.v0.Client(registry.host("token"))

