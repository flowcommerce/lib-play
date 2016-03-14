package io.flow.play.clients

/**
  * Gets an instance of the token client. Note that we cannot declare
  * a dependency on registry here as registry requires token to
  * authorize the user. Thus we create an instance of the token client
  * using the default base URL declared in apidoc.
  */
@javax.inject.Singleton
class DefaultTokenClient @javax.inject.Inject() () extends io.flow.token.v0.Client()
