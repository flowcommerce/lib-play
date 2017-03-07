package io.flow.play.util

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.common.v0.models.{Environment, Role, UserReference}
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
sealed trait Auth {

  private[this] val header = JwtHeader("HS256")

  /**
    * Timestamp is used to expire authorizations automatically
    */
  def createdAt: DateTime

  /**
    * In production, we set request id in the API Proxy, and it is
    * included as part of the auth header. Doing so allows us to trace a single
    * API request across all the service calls we make (assuming we propagate
    * the headers from auth data).
    */
  def requestId: String

  /**
    * Converts this auth data to a map containing only the keys with
    * non empty values.
    */
  protected def childAttributes: Map[String, Option[String]]

  /**
    * Converts this auth data to a valid JWT string using the provided
    * jwt salt.
    */
  def jwt(salt: String): String = {
    val all = Map(
    "request_id" -> requestId,
    "created_at" -> dateTime.print(createdAt)
    ) ++ childAttributes.flatMap { case (key, value) => value.map { v => key -> v } }

    val claimsSet = JwtClaimsSet(all)
    JsonWebToken(header, claimsSet, salt)
  }

}

object Auth {

  case class AnonymousAuth(
    override val requestId: String,
    user: Option[UserReference]
  ) extends Auth {
    override val createdAt: DateTime = DateTime.now
  }

  case class AnonymousOrgAuth(
    override val requestId: String,
    user: Option[UserReference],
    organization: Option[OrgData.AnonymousOrgData]
  ) extends Auth {
    override val createdAt: DateTime = DateTime.now

    override def childAttributes: Map[String, Option[String]] = {
      Map(
        "user_id" -> user.map(_.id),
        "organization" -> organization.map(_.organization),
        "environment" -> organization.map(_.environment.toString)
      )
    }
  }

  case class IdentifiedAuth(
    override val requestId: String,
    user: UserReference
  ) extends Auth {
    override val createdAt: DateTime = DateTime.now
  }

  case class IdentifiedOrgAuth(
    override val requestId: String,
    user: UserReference,
    organization: OrgData.IdentifiedOrgData
  ) extends Auth {
    override val createdAt: DateTime = DateTime.now
  }


}

sealed trait OrgData {
  def organization: String
  def environment: Environment
}

object OrgData {

  case class AnonymousOrgData(
    override val organization: String,
    override val environment: Environment
  ) extends OrgData

  case class IdentifiedOrgData(
    override val organization: String,
    override val environment: Environment,
    role: Role
  ) extends OrgData

}



