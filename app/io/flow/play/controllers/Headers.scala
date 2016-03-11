package io.flow.play.controllers

object Headers {

  val AuthorizationHeaderName = "Authorization"

  /**
    * If present, parses the basic authorization header and returns
    * its decoded value.
    */
  def basicAuthorizationToken(
    headers: play.api.mvc.Headers
  ): Option[Authorization] = {
    headers.get(AuthorizationHeaderName).flatMap { h =>
      Authorization.get(h)
    }
  }

}
