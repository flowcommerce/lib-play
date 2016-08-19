package io.flow.play.util

import authentikat.jwt.{JwtClaimsSet, JwtHeader, JsonWebToken}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat.dateTime

import javax.inject.{Inject, Singleton}

/**
  * Creates a valid X-Flow-Auth header for talking directly to a
  * service. Bound config must have a JWT_SALT parameter.
  */
@Singleton
class AuthHeaders @Inject() (
  config: Config
) {

  private[this] lazy val jwtSalt = config.requiredString("JWT_SALT")

  def headers(auth: AuthData) = Seq(
    AuthData.Header -> auth.jwt(jwtSalt)
  )
  
}


object AuthData {

  val Header = "X-Flow-Auth"

  /**
    * Helper to create a valid auth data for this user (no organization)
    */
  def user(requestId: String, user: UserReference): AuthData = {
    AuthData(
      requestId = requestId,
      createdAt = new DateTime(),
      user = user,
      organization = None
    )
  }

  /**
    * Helper to create a valid auth data for this user and organization.
    */
  def organization(
    requestId: String,
    user: UserReference,
    org: String,
    role: Role = Role.Member,
    environment: Environment = Environment.Sandbox
  ): AuthData = {
    AuthData(
      requestId = requestId,
      createdAt = new DateTime(),
      user = user,
      organization = Some(
        OrganizationAuthData(
          organization = org,
          role = role,
          environment = environment
        )
      )
    )
  }

}

/**
  * Represents the data securely authenticated by the API proxy
  * server. All of our software should depend on data from this object
  * when retrieved from the X-Flow-Auth header (as opposed, for
  * example, to relying on the organization id from the URL path).
  * 
  * The API Proxy server validates this data, and also guarantees that
  * the user is authorized to access information for the specified
  * organization.
  * 
  * @param requestId In production, we set request id in the API Proxy, and it is
  *        included as part of the auth header. Doing so allows us to trace a single
  *        API request across all the service calls we make (assuming we propagate
  *        the headers from auth data).
  */
case class AuthData(
  requestId: String,
  createdAt: DateTime,
  user: UserReference,
  organization: Option[OrganizationAuthData]
) {

  private[this] val header = JwtHeader("HS256")

  /**
    * Converts this auth data to a map containing only the keys with
    * their values.
    */
  def toMap(): Map[String, String] = {
    Map(
      "request_id" -> Some(requestId),
      "created_at" -> Some(dateTime.print(createdAt)),
      "user_id" -> Some(user.id),
      "organization" -> organization.map(_.organization),
      "role" -> organization.map(_.role.toString),
      "environment" -> organization.map(_.environment.toString)
    ).flatMap { case (key, value) => value.map { v => (key -> v)} }
  }

  /**
    * Converts this auth data to a valid JWT string using the provided
    * jwt salt.
    */
  def jwt(salt: String): String = {
    val claimsSet = JwtClaimsSet(toMap())
    JsonWebToken(header, claimsSet, salt)
  }

}

/**
 * Represents authorization data for a given organization.
 */
case class OrganizationAuthData(
  organization: String,
  role: Role,
  environment: Environment
)
