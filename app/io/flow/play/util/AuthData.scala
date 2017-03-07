package io.flow.play.util

import java.util.UUID

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.ISODateTimeFormat.dateTime
import play.api.Logger

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
sealed trait AuthData {

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

  /**
    * Parses data in the map to an appropriate type of AuthData
    */
  def fromMap(data: Map[String, String]): Option[AuthData] = {
    data.get("created_at").map { ts =>
      val createdAt = ISODateTimeFormat.dateTimeParser.parseDateTime(ts)
      val requestId = data.get("request_id").getOrElse {
        Logger.warn("JWT Token did not have a request_id - generated a new request id")
        "lib-play-" + UUID.randomUUID.toString
      }
      val user: Option[UserReference] = data.get("user_id").map(UserReference.apply)
      val organizationId: Option[String] = data.get("organization_id")

      val environment: Option[Environment] = data.get("Environment").map(Environment.apply).flatMap { e =>
        e match {
          case Environment.UNDEFINED(other) => {
            Logger.warn(s"Unexpected Environment[$other] - ignoring")
            None
          }
          case Environment.Production => Some(e)
          case Environment.Sandbox => Some(e)
        }
      }

      val role: Option[Role] = data.get("Role").map(Role.apply).flatMap { role =>
        role match {
          case Role.UNDEFINED(other) => {
            Logger.warn(s"Unexpected Role[$other] - ignoring")
            None
          }
          case Role.Member => Some(role)
          case Role.Admin => Some(role)
        }
      }

      (user, organizationId, environment, role) match {
        case (_, Some(o), Some(e), None) => AuthData.AnonymousOrgAuth(
          createdAt, requestId, user = user, organization = OrgData.AnonymousOrgData(organization = o, environment = e)
        )
        case (Some(u), Some(o), Some(e), Some(r)) => AuthData.IdentifiedOrgAuth(
          createdAt, requestId, user = u, OrgData.IdentifiedOrgData(organization = o, environment = e, role = r)
        )
        case (Some(u), _, _, _) => AuthData.IdentifiedAuth(createdAt, requestId, user = u)
        case (None, _, _, _) => AuthData.AnonymousAuth(createdAt, requestId, user = user)
      }
    }
  }
}

object AuthData {

  case class AnonymousAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: Option[UserReference]
  ) extends AuthData {

    override def childAttributes: Map[String, Option[String]] = {
      Map(
        "user_id" -> user.map(_.id)
      )
    }
  }

  case class AnonymousOrgAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: Option[UserReference],
    organization: OrgData.AnonymousOrgData
  ) extends AuthData {

    override def childAttributes: Map[String, Option[String]] = {
      Map(
        "user_id" -> user.map(_.id),
        "organization" -> Some(organization.organization),
        "environment" -> Some(organization.environment.toString)
      )
    }
  }

  case class IdentifiedAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference
  ) extends AuthData {

    override def childAttributes: Map[String, Option[String]] = {
      Map(
        "user_id" -> Some(user.id)
      )
    }
  }

  case class IdentifiedOrgAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference,
    orgData: OrgData.IdentifiedOrgData
  ) extends AuthData {

    override def childAttributes: Map[String, Option[String]] = {
      Map(
        "user_id" -> Some(user.id),
        "organization" -> Some(orgData.organization),
        "environment" -> Some(orgData.environment.toString),
        "role" -> Some(orgData.role.toString)
      )
    }
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