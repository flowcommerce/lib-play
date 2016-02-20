# lib-play
Library supporting building REST APIs on play framework.

Traits:
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

    go run ~/go/src/github.com/flowcommerce/tools/dev.go tag --label micro
    sbt publish

## Publishing a new snapshot for local development

    edit build.sbt and append -SNAPSHOT to version
    sbt +publishLocal


