package io.flow.play.clients

@javax.inject.Singleton
class DefaultUserClient @javax.inject.Inject() (
  registry: Registry
) extends io.flow.user.v0.Client(registry.host("user"))

