package io.flow.play.util

import io.flow.common.v0.models.{CustomerReference, Environment, Role, UserReference}
import io.flow.log.RollbarLogger
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.ISODateTimeFormat.dateTime
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim, JwtHeader}

case class AuthDataMap(
  requestId: String,
  createdAt: DateTime,
  session: Option[FlowSession] = None,
  user: Option[UserReference] = None,
  organization: Option[String] = None,
  channel: Option[String] = None,
  environment: Option[Environment] = None,
  role: Option[Role] = None,
  customer: Option[CustomerReference] = None
) {

  def toMap: Map[String, String] = {
    Map(
      AuthDataMap.Fields.RequestId -> Some(requestId),
      AuthDataMap.Fields.CreatedAt -> Some(dateTime.print(createdAt)),
      AuthDataMap.Fields.UserId -> user.map(_.id),
      AuthDataMap.Fields.Session -> session.map(_.id),
      AuthDataMap.Fields.Organization -> organization,
      AuthDataMap.Fields.Channel -> channel,
      AuthDataMap.Fields.Environment -> environment.map(_.toString),
      AuthDataMap.Fields.Role -> role.map(_.toString),
      AuthDataMap.Fields.Customer -> customer.map(_.number),
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
  
  private[this] val header = JwtHeader(JwtAlgorithm.HS256)

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

    val claimsSet: JwtClaim = JwtClaim() ++ (all.toMap.toSeq: _*)
    Jwt.encode(header, claimsSet, salt)
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
    val Channel = "channel"
    val Environment = "environment"
    val Role = "role"
    val Customer = "customer"
  }

  def fromMap[T <: AuthData](data: Map[String, String])(
    f: AuthDataMap => Option[T]
  )(implicit logger: RollbarLogger): Option[T] = {
    data.get("created_at").flatMap { ts =>
      val createdAt = ISODateTimeFormat.dateTimeParser.parseDateTime(ts)
      val requestId = data.getOrElse(Fields.RequestId, AuthHeaders.generateRequestId())
      val session = data.get(Fields.Session).map { id =>
        FlowSession(id = id)
      }
      val user: Option[UserReference] = data.get(Fields.UserId).map(UserReference.apply)
      val organizationId: Option[String] = data.get(Fields.Organization)
      val channelId: Option[String] = data.get(Fields.Channel)

      val environment: Option[Environment] = data.get(Fields.Environment).map(Environment.apply).flatMap { e =>
        e match {
          case Environment.UNDEFINED(other) => {
            logger.withKeyValue("env", other).warn("Unexpected Environment - ignoring")
            None
          }
          case Environment.Production => Some(e)
          case Environment.Sandbox => Some(e)
        }
      }

      val role: Option[Role] = data.get(Fields.Role).map(Role.apply).flatMap { role =>
        role match {
          case Role.UNDEFINED(other) => {
            logger.withKeyValue("role", other).warn(s"Unexpected Role - ignoring")
            None
          }
          case Role.Member => Some(role)
          case Role.Admin => Some(role)
        }
      }

      val customer = data.get(Fields.Customer).map { number =>
        CustomerReference(number = number)
      }

      f(
        AuthDataMap(
          createdAt = createdAt,
          requestId = requestId,
          session = session,
          user = user,
          organization = organizationId,
          channel = channelId,
          environment = environment,
          role = role,
          customer = customer
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
    session: Option[FlowSession],
    customer: Option[CustomerReference],
    organization: Option[String],
    channel: Option[String],
    environment: Option[Environment]
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = user,
        session = session,
        customer = customer,
        organization = organization,
        environment = environment
      )
    }

  }

  object Anonymous {

    val Empty = Anonymous(
      requestId = AuthHeaders.generateRequestId("anonymousrequest"),
      user = None,
      session = None,
      customer = None,
      organization = None,
      channel = None,
      environment = None
    )

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Anonymous] = {
      AuthDataMap.fromMap(data) { dm =>
        Some(
          Anonymous(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = dm.user,
            session = dm.session,
            customer = dm.customer,
            organization = dm.organization,
            channel = dm.channel,
            environment = dm.environment
          )
        )
      }
    }

  }

  case class Identified(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    user: UserReference,
    session: Option[FlowSession],
    customer: Option[CustomerReference]
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = Some(user),
        session = session,
        customer = customer
      )
    }

  }

  object Identified {

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Identified] = {
      AuthDataMap.fromMap(data) { dm =>
        dm.user.map { user =>
          Identified(
            createdAt = dm.createdAt,
            requestId = dm.requestId,
            user = user,
            session = dm.session,
            customer = dm.customer
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

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Session] = {
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

  case class Customer(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    session: FlowSession,
    customer: CustomerReference
  ) extends AuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        session = Some(session),
        customer = Some(customer)
      )
    }

  }

  object Customer {

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Customer] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.session, dm.customer) match {
          case (Some(session), Some(customer)) =>
            Some(Customer(
              createdAt = dm.createdAt,
              requestId = dm.requestId,
              session = session,
              customer = customer
            ))

          case _ => None
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

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Session] = {
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
    session: Option[FlowSession],
    customer: Option[CustomerReference]
  ) extends OrgAuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        organization = Some(organization),
        environment = Some(environment),
        user = Some(user),
        role = Some(role),
        session = session,
        customer = customer
      )
    }

  }

  object Identified {

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Identified] = {
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
                session = dm.session,
                customer = dm.customer
              )
            )
          }
          case _ => None
        }
      }
    }
  }

  case class Customer(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    override val organization: String,
    override val environment: Environment,
    session: FlowSession,
    customer: CustomerReference
  ) extends OrgAuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        organization = Some(organization),
        environment = Some(environment),
        session = Some(session),
        customer = Some(customer)
      )
    }
  }

  object Customer {

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[Customer] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.organization, dm.environment, dm.session, dm.customer) match {
          case (Some(org), Some(env), Some(session), Some(customer)) => {
            Some(Customer(
              createdAt = dm.createdAt,
              requestId = dm.requestId,
              session = session,
              organization = org,
              environment = env,
              customer = customer
            ))
          }
          case _ => None
        }
      }
    }
  }

  object Org {

    /**
      * Parses either an identified org or customer org or session org (or None)
      */
    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[io.flow.play.util.OrgAuthData] = {
      Identified.fromMap(data)
        .orElse(Customer.fromMap(data))
        .orElse(Session.fromMap(data))
    }
  }

  object IdentifiedCustomer {

    /**
      * Parses either an identified org or customer org (or None)
      */
    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[io.flow.play.util.OrgAuthData] = {
      Identified.fromMap(data)
        .orElse(Customer.fromMap(data))
    }
  }


  /**
    * Required wrapper to assist in migrating authorization in Checkout UI.
    * Authorization header may be passed as either a Bearer JWT (represented as a OrgAuthData.Customer)
    * or the legacy session (represented as an AuthData.Session)
    *
    * Note the different, intentionally referenced traits, OrgAuthData vs. AuthData
    */
  object Checkout {

    /**
      * Parses either a customer org or a session (or None)
      */
    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[io.flow.play.util.AuthData] = {
      OrgAuthData.Customer.fromMap(data)
        .orElse(AuthData.Session.fromMap(data))
    }
  }

  /**
    * Required wrapper to assist in migrating authorization in Checkout UI.
    * Authorization header may be passed as either a Bearer JWT (represented as a OrgAuthData.Customer)
    * or the legacy session (represented as an OrgAuthData.SessionOrg)
    */
  object CheckoutOrg {

    /**
      * Parses either a customer org or a session org (or None)
      */
    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[io.flow.play.util.OrgAuthData] = {
      OrgAuthData.Customer.fromMap(data)
        .orElse(OrgAuthData.Session.fromMap(data))
    }
  }

}

sealed trait ChannelAuthData extends AuthData {
  def channel: String
}

object ChannelAuthData {
  case class IdentifiedChannel(
    override val createdAt: DateTime = DateTime.now,
    override val requestId: String,
    channel: String,
    user: UserReference,
    role: Role,
    session: Option[FlowSession],
    customer: Option[CustomerReference]
  ) extends ChannelAuthData {

    override protected def decorate(base: AuthDataMap): AuthDataMap = {
      base.copy(
        user = Some(user),
        channel = Some(channel),
        role = Some(role),
      )
    }

  }

  object IdentifiedChannel {

    def fromMap(data: Map[String, String])(implicit logger: RollbarLogger): Option[IdentifiedChannel] = {
      AuthDataMap.fromMap(data) { dm =>
        (dm.user, dm.channel, dm.role) match {
          case (Some(user), Some(channel), Some(role)) => {
            Some(
              IdentifiedChannel(
                createdAt = dm.createdAt,
                requestId = dm.requestId,
                user = user,
                channel = channel,
                role = role,
                session = dm.session,
                customer = dm.customer
              )
            )
          }
          case _ => None
        }

      }
    }
  }
}