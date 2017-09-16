package io.flow.play.clients

/**
  * Gets an instance of the token client. Note that we cannot declare
  * a dependency on registry here as registry requires token to
  * authorize the user. Thus we create an instance of the token client
  * but explicitly set the port to known values for both development
  * and production.
  */
@javax.inject.Singleton
class DefaultTokenClient @javax.inject.Inject() () extends io.flow.token.v0.Client(
  RegistryConstants.host("token", 6151)
)
