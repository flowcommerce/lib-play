package io.flow.play.controllers

object Headers {

  val Authorization = "Authorization"

  /**
    * If present, parses the basic authorization header and returns
    * its decoded value.
    */
  def basicAuthorizationToken(
    headers: play.api.mvc.Headers
  ): Option[BasicAuthorization.Authorization] = {
    headers.get(Authorization).flatMap { h =>
      BasicAuthorization.get(h)
    }
  }

}
