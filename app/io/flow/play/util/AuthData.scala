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

  def fromMap[T <: AuthData](data: Map[String, String])(
    f: AuthDataMap => Option[T]
  ): Option[T] = {
    data.get("created_at").flatMap { ts =>
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

      f(
        AuthDataMap(
          createdAt = createdAt,
          requestId = requestId,
          user = user,
          organization = organizationId,
          environment = environment,
          role = role
        )
      )
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

  object AnonymousAuth {

    def fromMap(data: Map[String, String]): Option[AnonymousAuth] = {
      AuthDataMap.fromMap(data) { dm =>
        Some(
          AnonymousAuth(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = dm.user
          )
        )
      }
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

  object AnonymousOrgAuth {

    def fromMap(data: Map[String, String]): Option[AnonymousOrgAuth] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.organization, dm.environment) match {
          case (Some(org), Some(env)) => {
            Some(
              AnonymousOrgAuth(
                createdAt = dm.createdAt,
                requestId = dm.requestId,
                user = dm.user,
                orgData = OrgData.AnonymousOrgData(
                  organization = org,
                  environment = env
                )
              )
            )
          }
          case _ => None
        }
      }
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

  object IdentifiedAuth {

    def fromMap(data: Map[String, String]): Option[IdentifiedAuth] = {
      AuthDataMap.fromMap(data) { dm =>
        dm.user.map { user =>
          IdentifiedAuth(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = user
          )
        }
      }
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

  object IdentifiedOrgAuth {

    def fromMap(data: Map[String, String]): Option[IdentifiedOrgAuth] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.user, dm.organization, dm.environment, dm.role) match {
          case (Some(user), Some(org), Some(env), Some(role)) => {
            Some(
              IdentifiedOrgAuth(
                createdAt = dm.createdAt,
                requestId = dm.requestId,
                user = user,
                orgData = OrgData.IdentifiedOrgData(
                  organization = org,
                  environment = env,
                  role = role
                )
              )
            )
          }
          case _ => None
        }
      }
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
