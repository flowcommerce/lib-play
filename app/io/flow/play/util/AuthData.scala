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
  session: Option[FlowSession] = None,
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
      AuthDataMap.Fields.Session -> session.map(_.id),
      AuthDataMap.Fields.Organization -> organization,
      AuthDataMap.Fields.Environment -> environment.map(_.toString),
      AuthDataMap.Fields.Role -> role.map(_.toString)
    ).flatMap { case (k, value) => value.map { v => k -> v } }
  }

}

/**
  * Makes available key data from the flow session. These data
  * come from the JWT Headers usually set by the API proxy. We
  * do not make ALL session data available - but provide a base
  * class here to expose more information over time as it becomes
  * critical (as well as providing a strongly typed class to
  * store the session id)
  */
case class FlowSession(
  id: String
) {
  assert(
    id.startsWith(Constants.Prefixes.Session),
    s"Flow session id must start with '${Constants.Prefixes.Session}' and not[${id.substring(0, 3)}]"
  )
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

sealed trait OrgAuthData extends AuthData {
  def organization: String
  def environment: Environment
}

object AuthDataMap {

  object Fields {
    val RequestId = "request_id"
    val CreatedAt = "created_at"
    val UserId = "user_id"
    val Session = "session"
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
      val session = data.get(Fields.Session).map { id =>
        FlowSession(id = id)
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
          session = session,
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

  case class Anonymous(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: Option[UserReference],
    session: Option[FlowSession]
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = user,
        session = session
      )
    }

  }

  object Anonymous {

    def empty(): Anonymous = {
      Anonymous(
        requestId = AuthHeaders.generateRequestId("anonymousrequest"),
        user = None,
        session = None
      )
    }

    def fromMap(data: Map[String, String]): Option[Anonymous] = {
      AuthDataMap.fromMap(data) { dm =>
        Some(
          Anonymous(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = dm.user,
            session = dm.session
          )
        )
      }
    }

  }

  case class Identified(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference,
    session: Option[FlowSession]
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = Some(user),
        session = session
      )
    }

  }

  object Identified {

    def fromMap(data: Map[String, String]): Option[Identified] = {
      AuthDataMap.fromMap(data) { dm =>
        dm.user.map { user =>
          Identified(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = user,
            session = dm.session
          )
        }
      }
    }

  }

  case class Session(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    session: FlowSession
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        session = Some(session)
      )
    }

  }

  object Session {

    def fromMap(data: Map[String, String]): Option[Session] = {
      AuthDataMap.fromMap(data) { dm =>
        dm.session.map { session =>
          Session(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            session = session
          )
        }
      }
    }

  }
}

object OrgAuthData {

  case class Session(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    override val organization: String,
    override val environment: Environment,
    session: FlowSession
  ) extends OrgAuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        organization = Some(organization),
        environment = Some(environment),
        session = Some(session)
      )
    }
  }

  object Session {

    def fromMap(data: Map[String, String]): Option[Session] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.organization, dm.environment) match {
          case (Some(org), Some(env)) => {
            dm.session.map { session =>
              Session(
                createdAt = dm.createdAt,
                requestId = dm.requestId,
                session = session,
                organization = org,
                environment = env
              )
            }
          }
          case _ => None
        }
      }
    }
  }

  case class Identified(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    override val organization: String,
    override val environment: Environment,
    user: UserReference,
    role: Role,
    session: Option[FlowSession]
  ) extends OrgAuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        organization = Some(organization),
        environment = Some(environment),
        user = Some(user),
        role = Some(role),
        session = session
      )
    }

  }

  object Identified {

    def fromMap(data: Map[String, String]): Option[Identified] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.user, dm.organization, dm.environment, dm.role) match {
          case (Some(user), Some(org), Some(env), Some(role)) => {
            Some(
              Identified(
                createdAt = dm.createdAt,
                requestId = dm.requestId,
                user = user,
                organization = org,
                environment = env,
                role = role,
                session = dm.session
              )
            )
          }
          case _ => None
        }
      }
    }
  }

  object Org {

    /**
      * Parses either an identified org or session org (or None)
      */
    def fromMap(data: Map[String, String]): Option[io.flow.play.util.OrgAuthData] = {
      Identified.fromMap(data) match {
        case None => Session.fromMap(data)
        case Some(auth) => Some(auth)
      }
    }
  }

}
