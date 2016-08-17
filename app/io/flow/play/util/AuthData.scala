package io.flow.play.util

import authentikat.jwt.{JwtClaimsSet, JwtHeader, JsonWebToken}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat.dateTime

object AuthData {

  /**
    * Helper to create a valid auth data for this user (no organization)
    */
  def user(user: UserReference): AuthData = {
    AuthData(
      createdAt = new DateTime(),
      user = user,
      organization = None
    )
  }

  /**
    * Helper to create a valid auth data for this user and organization.
    */
  def organization(
    user: UserReference,
    org: String,
    role: Role = Role.Member,
    environment: Environment = Environment.Sandbox
  ): AuthData = {
    AuthData(
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
  */
case class AuthData(
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
