[![Build Status](https://travis-ci.com/flowcommerce/lib-play.svg?token=8bzVqzHy6JVEQr9mN9hx&branch=master)](https://travis-ci.com/flowcommerce/lib-play)

# lib-play
Library supporting building REST APIs on play framework.

# Provided Bindings

    play.modules.enabled += "io.flow.play.clients.ConfigModule"
    play.modules.enabled += "io.flow.play.clients.RegistryModule"
    play.modules.enabled += "io.flow.play.clients.TokenModule"
    play.modules.enabled += "io.flow.play.clients.UserModule"

# Traits

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

    dev tag --label micro
    sbt publish

## Publishing a new snapshot for local development

    edit build.sbt and append -SNAPSHOT to version
    sbt +publishLocal


