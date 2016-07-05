[![Build Status](https://travis-ci.org/flowcommerce/lib-play.svg?branch=master)](https://travis-ci.com/flowcommerce/lib-play)

# lib-play
Library supporting building REST APIs on play framework.

## Provided Bindings

    play.modules.enabled += "io.flow.play.clients.ConfigModule"
    play.modules.enabled += "io.flow.play.clients.RegistryModule"
    play.modules.enabled += "io.flow.play.clients.TokenModule"
    play.modules.enabled += "io.flow.play.clients.UserModule"

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

## Publishing a new version

    go run release.go

## Publishing a new snapshot for local development

    edit build.sbt and append -SNAPSHOT to version
    sbt +publishLocal
