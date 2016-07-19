package io.flow.play.util

import authentikat.jwt.{JwtClaimsSet, JwtHeader, JsonWebToken}
import io.flow.common.v0.models.{Environment, Role}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat.dateTime

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
  userId: String,
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
      "user_id" -> Some(userId),
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
