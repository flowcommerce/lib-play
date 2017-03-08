package io.flow.play.util

import java.util.UUID

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import io.flow.common.v0.models.{Environment, Role, UserReference}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.ISODateTimeFormat.dateTime
import play.api.Logger

case class AuthDataMap(
  requestId: String,
  createdAt: DateTime,
  user: Option[UserReference] = None,
  organization: Option[String] = None,
  environment: Option[Environment] = None,
  role: Option[Role] = None
) {

  def toMap: Map[String, String] = {
    Map(
      AuthDataMap.Fields.RequestId -> Some(requestId),
      AuthDataMap.Fields.CreatedAt -> Some(dateTime.print(createdAt)),
      AuthDataMap.Fields.UserId -> user.map(_.id),
      AuthDataMap.Fields.Organization -> organization,
      AuthDataMap.Fields.Environment -> environment.map(_.toString),
      AuthDataMap.Fields.Role -> role.map(_.toString)
    ).flatMap { case (k, value) => value.map { v => k -> v } }
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
    * Add specific data to assist in serialization to jwt map
    */
  protected def decorate(base: AuthDataMap): AuthDataMap

  /**
    * Converts this auth data to a valid JWT string using the provided
    * jwt salt.
    */
  def jwt(salt: String): String = {
    val all = decorate(
      AuthDataMap(
        requestId = requestId,
        createdAt = createdAt
      )
    )

    val claimsSet = JwtClaimsSet(all.toMap)
    JsonWebToken(header, claimsSet, salt)
  }

}

object AuthDataMap {

  object Fields {
    val RequestId = "request_id"
    val CreatedAt = "created_at"
    val UserId = "user_id"
    val Organization = "organization"
    val Environment = "environment"
    val Role = "role"
  }

  /**
    * Parses data in the map to an appropriate type of AuthData
    */
  //noinspection GetGetOrElse
  def fromMap(data: Map[String, String]): Option[AuthData] = {
    data.get("created_at").map { ts =>
      val createdAt = ISODateTimeFormat.dateTimeParser.parseDateTime(ts)
      val requestId = data.get(Fields.RequestId).getOrElse {
        Logger.warn("JWT Token did not have a request_id - generated a new request id")
        "lib-play-" + UUID.randomUUID.toString
      }
      val user: Option[UserReference] = data.get(Fields.UserId).map(UserReference.apply)
      val organizationId: Option[String] = data.get(Fields.Organization)

      val environment: Option[Environment] = data.get(Fields.Environment).map(Environment.apply).flatMap { e =>
        e match {
          case Environment.UNDEFINED(other) => {
            Logger.warn(s"Unexpected Environment[$other] - ignoring")
            None
          }
          case Environment.Production => Some(e)
          case Environment.Sandbox => Some(e)
        }
      }

      val role: Option[Role] = data.get(Fields.Role).map(Role.apply).flatMap { role =>
        role match {
          case Role.UNDEFINED(other) => {
            Logger.warn(s"Unexpected Role[$other] - ignoring")
            None
          }
          case Role.Member => Some(role)
          case Role.Admin => Some(role)
        }
      }

      println(s"user[${user}] organizationId[$organizationId] environment[$environment] role[$role]")

      (user, organizationId, environment, role) match {
        case (_, Some(o), Some(e), None) => AuthData.AnonymousOrgAuth(
          createdAt, requestId, user = user, orgData = OrgData.AnonymousOrgData(organization = o, environment = e)
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

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = user
      )
    }
  }

  case class AnonymousOrgAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: Option[UserReference],
    orgData: OrgData.AnonymousOrgData
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = user,
        organization = Some(orgData.organization),
        environment = Some(orgData.environment)
      )
    }

  }

  case class IdentifiedAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = Some(user)
      )
    }

  }

  case class IdentifiedOrgAuth(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference,
    orgData: OrgData.IdentifiedOrgData
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = Some(user),
        organization = Some(orgData.organization),
        environment = Some(orgData.environment),
        role = Some(orgData.role)
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