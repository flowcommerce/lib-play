[![Build Status](https://travis-ci.org/flowcommerce/lib-play.svg?branch=main)](https://travis-ci.com/flowcommerce/lib-play)

# lib-play
Library supporting building REST APIs on play framework.

## Provided Bindings

    play.modules.enabled += "io.flow.play.clients.ConfigModule"
    play.modules.enabled += "io.flow.play.clients.RegistryModule"
    play.modules.enabled += "io.flow.play.clients.TokenModule"
    play.modules.enabled += "io.flow.play.clients.UserModule"

## Request Logging

To enable request logging, add the following to your
play application config:

    play.http.filters=io.flow.play.util.LoggingFilter

By default, all HTTP methods will be logged. To only
log a specific subset of methods, add the following:

    play.http.logging.methods=["GET","PUT"]

## CORS

A CORS request handler is provided. To enable, add the following
to your play application config (instead of `LoggingFilter` above):

    play.http.filters=io.flow.play.util.CorsWithLoggingFilter
    play.filters.cors.allowedHttpMethods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]

## Global error handler for JSON APIs

This error handler capture client and server errors, always
returning application/json. This handler also makes sure to
log server error messages and assigns a unique error number
that is returned to the user. This error number is also logged
making it easy to find a particular error in the log.

Additionally, if flow environment is development, the actual contents
of the exception will be returned (disabled in production to avoid
leaking information).

    play.http.errorHandler = "io.flow.play.util.ErrorHandler"

## Traits

  Anonymous Controller

   -- Provides user(...) method to get an instance of the current
      user.
    -- (type: Option[User])

  Identified Controller
    -- extends Anonymous
    -- requires a user to be present
    -- (type: User)

  XXX RestController
    -- assumes basic authorization for the toke
       Default implementation uses user token through basic
       authorization

## SQS Actor Proxy

When splitting out background processing, it became apparent that we needed to have a way
to send messages from an API service to actors running in the background processing jobs service.

With minimal amount of refactoring required, we can do this by binding a proxy actor to the original
actor name in the API service, which send all messages it gets over SQS to a receiver in the jobs service.
The receiver will forward all messages received on its SQS queue to the original actor that now lives in
the jobs service.

To use this, you need to add the `ActorProxyGuiceSupport` to your Actor guice module. Then you can bind your
proxy sender to the original actor name, where `@Named("test-actor") actorRef: ActorRef` is used.

```
- bindActor[TestActor]("test-actor")
+ bindActorProxySender("test-actor", ReactiveActorProxySerde)
```

Then in your jobs service you can bind your original actor with a proxy receiver.

```
+ bindActor[TestActor]("test-actor")
+ bindActorProxyReceiver(
+   name = "test-actor-receiver",
+   proxiedName = "test-actor",
+   serde = ReactiveActorProxySerde
+ )
```

Once that is done, you can send messages as you normally would, as long as your `ProxySerde` passed
to your sender and receiver, knows how to serialize and deserialize the messages.

```
class TestActorCaller @Inject() (@Named("test-actor") actor: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Tick =>
      // the following really send the message to the proxy we have bound to "test-actor"
      actor ! ReactiveActor.Messages.Changed
  }
}


class TestActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case ReactiveActor.Messages.Changed => log.info("RECEIVED TEST ACTOR CHANGED MESSAGE!")
  }
}
```

_NOTE: The sender and receiver will use the service name and the proxied receiver actor name to create an SQS queue_


## Publishing a new version

    go run release.go

## Publishing a new snapshot for local development

    edit build.sbt and append -SNAPSHOT to version
    sbt +publishLocal
