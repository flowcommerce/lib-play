# lib-play
Library supporting building REST APIs on play framework.

Traits:
  Anonyous Controller

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

  AuthorizedXXXController
    -- mixes in the authorization service
    
