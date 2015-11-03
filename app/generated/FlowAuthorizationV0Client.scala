/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.1-dev
 * apidoc:0.9.43 http://www.apidoc.me/flow/authorization/0.0.1-dev/play_2_4_client
 */
package io.flow.authorization.v0.models {

  sealed trait Authorization

  case class AuthorizationForm(
    privilege: io.flow.authorization.v0.models.Privilege,
    userGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
    roleGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
    context: String
  )

  case class AuthorizationRole(
    guid: _root_.java.util.UUID,
    privilege: io.flow.authorization.v0.models.Privilege,
    context: String,
    audit: io.flow.common.v0.models.Audit,
    role: io.flow.authorization.v0.models.Role
  ) extends Authorization

  case class AuthorizationUser(
    guid: _root_.java.util.UUID,
    privilege: io.flow.authorization.v0.models.Privilege,
    context: String,
    audit: io.flow.common.v0.models.Audit,
    user: io.flow.common.v0.models.Reference
  ) extends Authorization

  case class Check(
    result: Boolean,
    reason: String
  )

  case class Membership(
    guid: _root_.java.util.UUID,
    role: io.flow.authorization.v0.models.Role,
    user: io.flow.common.v0.models.Reference,
    audit: io.flow.common.v0.models.Audit
  )

  case class MembershipForm(
    roleGuid: _root_.java.util.UUID,
    userGuid: _root_.java.util.UUID
  )

  case class Role(
    guid: _root_.java.util.UUID,
    name: String,
    audit: io.flow.common.v0.models.Audit
  )

  case class RoleForm(
    name: String
  )

  /**
   * Provides future compatibility in clients - in the future, when a type is added
   * to the union Authorization, it will need to be handled in the client code. This
   * implementation will deserialize these future types as an instance of this class.
   */
  case class AuthorizationUndefinedType(
    description: String
  ) extends Authorization

  sealed trait Privilege

  object Privilege {

    case object All extends Privilege { override def toString = "all" }
    case object Create extends Privilege { override def toString = "create" }
    case object Read extends Privilege { override def toString = "read" }
    case object Update extends Privilege { override def toString = "update" }
    case object Delete extends Privilege { override def toString = "delete" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends Privilege

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(All, Create, Read, Update, Delete)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): Privilege = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[Privilege] = byName.get(value.toLowerCase)

  }

}

package io.flow.authorization.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.authorization.v0.models.json._
    import io.flow.common.v0.models.json._

    private[v0] implicit val jsonReadsUUID = __.read[String].map(java.util.UUID.fromString)

    private[v0] implicit val jsonWritesUUID = new Writes[java.util.UUID] {
      def writes(x: java.util.UUID) = JsString(x.toString)
    }

    private[v0] implicit val jsonReadsJodaDateTime = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateTimeParser
      dateTimeParser.parseDateTime(str)
    }

    private[v0] implicit val jsonWritesJodaDateTime = new Writes[org.joda.time.DateTime] {
      def writes(x: org.joda.time.DateTime) = {
        import org.joda.time.format.ISODateTimeFormat.dateTime
        val str = dateTime.print(x)
        JsString(str)
      }
    }

    implicit val jsonReadsAuthorizationPrivilege = __.read[String].map(Privilege.apply)
    implicit val jsonWritesAuthorizationPrivilege = new Writes[Privilege] {
      def writes(x: Privilege) = JsString(x.toString)
    }

    implicit def jsonReadsAuthorizationAuthorizationForm: play.api.libs.json.Reads[AuthorizationForm] = {
      (
        (__ \ "privilege").read[io.flow.authorization.v0.models.Privilege] and
        (__ \ "user_guid").readNullable[_root_.java.util.UUID] and
        (__ \ "role_guid").readNullable[_root_.java.util.UUID] and
        (__ \ "context").read[String]
      )(AuthorizationForm.apply _)
    }

    implicit def jsonWritesAuthorizationAuthorizationForm: play.api.libs.json.Writes[AuthorizationForm] = {
      (
        (__ \ "privilege").write[io.flow.authorization.v0.models.Privilege] and
        (__ \ "user_guid").writeNullable[_root_.java.util.UUID] and
        (__ \ "role_guid").writeNullable[_root_.java.util.UUID] and
        (__ \ "context").write[String]
      )(unlift(AuthorizationForm.unapply _))
    }

    implicit def jsonReadsAuthorizationAuthorizationRole: play.api.libs.json.Reads[AuthorizationRole] = {
      (
        (__ \ "guid").read[_root_.java.util.UUID] and
        (__ \ "privilege").read[io.flow.authorization.v0.models.Privilege] and
        (__ \ "context").read[String] and
        (__ \ "audit").read[io.flow.common.v0.models.Audit] and
        (__ \ "role").read[io.flow.authorization.v0.models.Role]
      )(AuthorizationRole.apply _)
    }

    implicit def jsonWritesAuthorizationAuthorizationRole: play.api.libs.json.Writes[AuthorizationRole] = {
      (
        (__ \ "guid").write[_root_.java.util.UUID] and
        (__ \ "privilege").write[io.flow.authorization.v0.models.Privilege] and
        (__ \ "context").write[String] and
        (__ \ "audit").write[io.flow.common.v0.models.Audit] and
        (__ \ "role").write[io.flow.authorization.v0.models.Role]
      )(unlift(AuthorizationRole.unapply _))
    }

    implicit def jsonReadsAuthorizationAuthorizationUser: play.api.libs.json.Reads[AuthorizationUser] = {
      (
        (__ \ "guid").read[_root_.java.util.UUID] and
        (__ \ "privilege").read[io.flow.authorization.v0.models.Privilege] and
        (__ \ "context").read[String] and
        (__ \ "audit").read[io.flow.common.v0.models.Audit] and
        (__ \ "user").read[io.flow.common.v0.models.Reference]
      )(AuthorizationUser.apply _)
    }

    implicit def jsonWritesAuthorizationAuthorizationUser: play.api.libs.json.Writes[AuthorizationUser] = {
      (
        (__ \ "guid").write[_root_.java.util.UUID] and
        (__ \ "privilege").write[io.flow.authorization.v0.models.Privilege] and
        (__ \ "context").write[String] and
        (__ \ "audit").write[io.flow.common.v0.models.Audit] and
        (__ \ "user").write[io.flow.common.v0.models.Reference]
      )(unlift(AuthorizationUser.unapply _))
    }

    implicit def jsonReadsAuthorizationCheck: play.api.libs.json.Reads[Check] = {
      (
        (__ \ "result").read[Boolean] and
        (__ \ "reason").read[String]
      )(Check.apply _)
    }

    implicit def jsonWritesAuthorizationCheck: play.api.libs.json.Writes[Check] = {
      (
        (__ \ "result").write[Boolean] and
        (__ \ "reason").write[String]
      )(unlift(Check.unapply _))
    }

    implicit def jsonReadsAuthorizationMembership: play.api.libs.json.Reads[Membership] = {
      (
        (__ \ "guid").read[_root_.java.util.UUID] and
        (__ \ "role").read[io.flow.authorization.v0.models.Role] and
        (__ \ "user").read[io.flow.common.v0.models.Reference] and
        (__ \ "audit").read[io.flow.common.v0.models.Audit]
      )(Membership.apply _)
    }

    implicit def jsonWritesAuthorizationMembership: play.api.libs.json.Writes[Membership] = {
      (
        (__ \ "guid").write[_root_.java.util.UUID] and
        (__ \ "role").write[io.flow.authorization.v0.models.Role] and
        (__ \ "user").write[io.flow.common.v0.models.Reference] and
        (__ \ "audit").write[io.flow.common.v0.models.Audit]
      )(unlift(Membership.unapply _))
    }

    implicit def jsonReadsAuthorizationMembershipForm: play.api.libs.json.Reads[MembershipForm] = {
      (
        (__ \ "role_guid").read[_root_.java.util.UUID] and
        (__ \ "user_guid").read[_root_.java.util.UUID]
      )(MembershipForm.apply _)
    }

    implicit def jsonWritesAuthorizationMembershipForm: play.api.libs.json.Writes[MembershipForm] = {
      (
        (__ \ "role_guid").write[_root_.java.util.UUID] and
        (__ \ "user_guid").write[_root_.java.util.UUID]
      )(unlift(MembershipForm.unapply _))
    }

    implicit def jsonReadsAuthorizationRole: play.api.libs.json.Reads[Role] = {
      (
        (__ \ "guid").read[_root_.java.util.UUID] and
        (__ \ "name").read[String] and
        (__ \ "audit").read[io.flow.common.v0.models.Audit]
      )(Role.apply _)
    }

    implicit def jsonWritesAuthorizationRole: play.api.libs.json.Writes[Role] = {
      (
        (__ \ "guid").write[_root_.java.util.UUID] and
        (__ \ "name").write[String] and
        (__ \ "audit").write[io.flow.common.v0.models.Audit]
      )(unlift(Role.unapply _))
    }

    implicit def jsonReadsAuthorizationRoleForm: play.api.libs.json.Reads[RoleForm] = {
      (__ \ "name").read[String].map { x => new RoleForm(name = x) }
    }

    implicit def jsonWritesAuthorizationRoleForm: play.api.libs.json.Writes[RoleForm] = new play.api.libs.json.Writes[RoleForm] {
      def writes(x: RoleForm) = play.api.libs.json.Json.obj(
        "name" -> play.api.libs.json.Json.toJson(x.name)
      )
    }

    implicit def jsonReadsAuthorizationAuthorization: play.api.libs.json.Reads[Authorization] = {
      (
        (__ \ "authorization_role").read(jsonReadsAuthorizationAuthorizationRole).asInstanceOf[play.api.libs.json.Reads[Authorization]]
        orElse
        (__ \ "authorization_user").read(jsonReadsAuthorizationAuthorizationUser).asInstanceOf[play.api.libs.json.Reads[Authorization]]
      )
    }

    implicit def jsonWritesAuthorizationAuthorization: play.api.libs.json.Writes[Authorization] = new play.api.libs.json.Writes[Authorization] {
      def writes(obj: Authorization) = obj match {
        case x: io.flow.authorization.v0.models.AuthorizationRole => play.api.libs.json.Json.obj("authorization_role" -> jsonWritesAuthorizationAuthorizationRole.writes(x))
        case x: io.flow.authorization.v0.models.AuthorizationUser => play.api.libs.json.Json.obj("authorization_user" -> jsonWritesAuthorizationAuthorizationUser.writes(x))
        case x: io.flow.authorization.v0.models.AuthorizationUndefinedType => sys.error(s"The type[io.flow.authorization.v0.models.AuthorizationUndefinedType] should never be serialized")
      }
    }
  }
}

package io.flow.authorization.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}
    import org.joda.time.{DateTime, LocalDate}
    import org.joda.time.format.ISODateTimeFormat
    import io.flow.authorization.v0.models._

    // Type: date-time-iso8601
    implicit val pathBindableTypeDateTimeIso8601 = new PathBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    implicit val queryStringBindableTypeDateTimeIso8601 = new QueryStringBindable.Parsing[org.joda.time.DateTime](
      ISODateTimeFormat.dateTimeParser.parseDateTime(_), _.toString, (key: String, e: Exception) => s"Error parsing date time $key. Example: 2014-04-29T11:56:52Z"
    )

    // Type: date-iso8601
    implicit val pathBindableTypeDateIso8601 = new PathBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )

    implicit val queryStringBindableTypeDateIso8601 = new QueryStringBindable.Parsing[org.joda.time.LocalDate](
      ISODateTimeFormat.yearMonthDay.parseLocalDate(_), _.toString, (key: String, e: Exception) => s"Error parsing date $key. Example: 2014-04-29"
    )

    // Enum: Privilege
    private[this] val enumPrivilegeNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.authorization.v0.models.Privilege.all.mkString(", ")}"

    implicit val pathBindableEnumPrivilege = new PathBindable.Parsing[io.flow.authorization.v0.models.Privilege] (
      Privilege.fromString(_).get, _.toString, enumPrivilegeNotFound
    )

    implicit val queryStringBindableEnumPrivilege = new QueryStringBindable.Parsing[io.flow.authorization.v0.models.Privilege](
      Privilege.fromString(_).get, _.toString, enumPrivilegeNotFound
    )

  }

}


package io.flow.authorization.v0 {

  object Constants {

    val UserAgent = "apidoc:0.9.43 http://www.apidoc.me/flow/authorization/0.0.1-dev/play_2_4_client"
    val Version = "0.0.1-dev"
    val VersionMajor = 0

  }

  class Client(
    apiUrl: String,
    auth: scala.Option[io.flow.authorization.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) {
    import io.flow.authorization.v0.models.json._
    import io.flow.common.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.authorization.v0.Client")

    logger.info(s"Initializing io.flow.authorization.v0.Client for url $apiUrl")

    def authorizations: Authorizations = Authorizations

    def checks: Checks = Checks

    def ioFlowCommonV0ModelsHealthchecks: IoFlowCommonV0ModelsHealthchecks = IoFlowCommonV0ModelsHealthchecks

    def memberships: Memberships = Memberships

    def roles: Roles = Roles

    object Authorizations extends Authorizations {
      override def get(
        guid: _root_.scala.Option[_root_.java.util.UUID] = None,
        guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
        userGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
        roleGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
        impliedUserGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
        limit: Long = 25,
        offset: Long = 0
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Authorization]] = {
        val queryParameters = Seq(
          guid.map("guid" -> _.toString),
          userGuid.map("user_guid" -> _.toString),
          roleGuid.map("role_guid" -> _.toString),
          impliedUserGuid.map("implied_user_guid" -> _.toString),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString)
        ).flatten ++
          guids.getOrElse(Nil).map("guids" -> _.toString)

        _executeRequest("GET", s"/authorizations", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("Seq[io.flow.authorization.v0.models.Authorization]", r, _.validate[Seq[io.flow.authorization.v0.models.Authorization]])
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }

      override def getByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Authorization] = {
        _executeRequest("GET", s"/authorizations/${guid}").map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Authorization", r, _.validate[io.flow.authorization.v0.models.Authorization])
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 404")
        }
      }

      override def post(
        authorizationForm: io.flow.authorization.v0.models.AuthorizationForm
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Authorization] = {
        val payload = play.api.libs.json.Json.toJson(authorizationForm)

        _executeRequest("POST", s"/authorizations", body = Some(payload)).map {
          case r if r.status == 201 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Authorization", r, _.validate[io.flow.authorization.v0.models.Authorization])
          case r if r.status == 409 => throw new io.flow.authorization.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 201, 409")
        }
      }

      override def deleteByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/authorizations/${guid}").map {
          case r if r.status == 204 => ()
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 404")
        }
      }
    }

    object Checks extends Checks {
      override def get(
        userGuid: _root_.java.util.UUID,
        privilege: io.flow.authorization.v0.models.Privilege,
        context: String
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Check] = {
        val queryParameters = Seq(
          Some("user_guid" -> userGuid.toString),
          Some("privilege" -> privilege.toString),
          Some("context" -> context)
        ).flatten

        _executeRequest("GET", s"/checks", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Check", r, _.validate[io.flow.authorization.v0.models.Check])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 409 => throw new io.flow.authorization.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 409")
        }
      }
    }

    object IoFlowCommonV0ModelsHealthchecks extends IoFlowCommonV0ModelsHealthchecks {
      override def getInternalAndHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = {
        _executeRequest("GET", s"/_internal_/healthcheck").map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.common.v0.models.Healthcheck", r, _.validate[io.flow.common.v0.models.Healthcheck])
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }
    }

    object Memberships extends Memberships {
      override def get(
        guid: _root_.scala.Option[_root_.java.util.UUID] = None,
        guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
        roleGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
        userGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
        limit: Long = 25,
        offset: Long = 0
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Membership]] = {
        val queryParameters = Seq(
          guid.map("guid" -> _.toString),
          roleGuid.map("role_guid" -> _.toString),
          userGuid.map("user_guid" -> _.toString),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString)
        ).flatten ++
          guids.getOrElse(Nil).map("guids" -> _.toString)

        _executeRequest("GET", s"/memberships", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("Seq[io.flow.authorization.v0.models.Membership]", r, _.validate[Seq[io.flow.authorization.v0.models.Membership]])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401")
        }
      }

      override def getByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Membership] = {
        _executeRequest("GET", s"/memberships/${guid}").map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Membership", r, _.validate[io.flow.authorization.v0.models.Membership])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def post(
        membershipForm: io.flow.authorization.v0.models.MembershipForm
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Membership] = {
        val payload = play.api.libs.json.Json.toJson(membershipForm)

        _executeRequest("POST", s"/memberships", body = Some(payload)).map {
          case r if r.status == 201 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Membership", r, _.validate[io.flow.authorization.v0.models.Membership])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 409 => throw new io.flow.authorization.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 201, 401, 409")
        }
      }

      override def deleteByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/memberships/${guid}").map {
          case r if r.status == 204 => ()
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 401, 404")
        }
      }
    }

    object Roles extends Roles {
      override def get(
        guid: _root_.scala.Option[_root_.java.util.UUID] = None,
        guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
        name: _root_.scala.Option[String] = None,
        limit: Long = 25,
        offset: Long = 0
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Role]] = {
        val queryParameters = Seq(
          guid.map("guid" -> _.toString),
          name.map("name" -> _),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString)
        ).flatten ++
          guids.getOrElse(Nil).map("guids" -> _.toString)

        _executeRequest("GET", s"/roles", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("Seq[io.flow.authorization.v0.models.Role]", r, _.validate[Seq[io.flow.authorization.v0.models.Role]])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401")
        }
      }

      override def getByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Role] = {
        _executeRequest("GET", s"/roles/${guid}").map {
          case r if r.status == 200 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Role", r, _.validate[io.flow.authorization.v0.models.Role])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def post(
        roleForm: io.flow.authorization.v0.models.RoleForm
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Role] = {
        val payload = play.api.libs.json.Json.toJson(roleForm)

        _executeRequest("POST", s"/roles", body = Some(payload)).map {
          case r if r.status == 201 => _root_.io.flow.authorization.v0.Client.parseJson("io.flow.authorization.v0.models.Role", r, _.validate[io.flow.authorization.v0.models.Role])
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 409 => throw new io.flow.authorization.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 201, 401, 409")
        }
      }

      override def deleteByGuid(
        guid: _root_.java.util.UUID
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/roles/${guid}").map {
          case r if r.status == 204 => ()
          case r if r.status == 401 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.authorization.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 401, 404")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {
      import play.api.Play.current

      val holder = play.api.libs.ws.WS.url(apiUrl + path).withHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).withHeaders(defaultHeaders : _*)
      auth.fold(holder) {
        case Authorization.Basic(username, password) => {
          holder.withAuth(username, password.getOrElse(""), play.api.libs.ws.WSAuthScheme.BASIC)
        }
        case a => sys.error("Invalid authorization scheme[" + a.getClass + "]")
      }
    }

    def _logRequest(method: String, req: play.api.libs.ws.WSRequest)(implicit ec: scala.concurrent.ExecutionContext): play.api.libs.ws.WSRequest = {
      val queryComponents = for {
        (name, values) <- req.queryString
        value <- values
      } yield s"$name=$value"
      val url = s"${req.url}${queryComponents.mkString("?", "&", "")}"
      auth.fold(logger.info(s"curl -X $method $url")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' $url")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Seq.empty,
      body: Option[play.api.libs.json.JsValue] = None
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).withQueryString(queryParameters:_*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).withQueryString(queryParameters:_*).withHeaders("Content-Type" -> "application/json; charset=UTF-8")).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).withQueryString(queryParameters:_*).withHeaders("Content-Type" -> "application/json; charset=UTF-8")).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).withQueryString(queryParameters:_*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).withQueryString(queryParameters:_*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).withQueryString(queryParameters:_*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).withQueryString(queryParameters:_*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).withQueryString(queryParameters:_*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

  }

  object Client {

    def parseJson[T](
      className: String,
      r: play.api.libs.ws.WSResponse,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.body)) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw new io.flow.authorization.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization
  object Authorization {
    case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  trait Authorizations {
    def get(
      guid: _root_.scala.Option[_root_.java.util.UUID] = None,
      guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
      userGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
      roleGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
      impliedUserGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
      limit: Long = 25,
      offset: Long = 0
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Authorization]]

    def getByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Authorization]

    def post(
      authorizationForm: io.flow.authorization.v0.models.AuthorizationForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Authorization]

    def deleteByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  trait Checks {
    def get(
      userGuid: _root_.java.util.UUID,
      privilege: io.flow.authorization.v0.models.Privilege,
      context: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Check]
  }

  trait IoFlowCommonV0ModelsHealthchecks {
    def getInternalAndHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck]
  }

  trait Memberships {
    def get(
      guid: _root_.scala.Option[_root_.java.util.UUID] = None,
      guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
      roleGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
      userGuid: _root_.scala.Option[_root_.java.util.UUID] = None,
      limit: Long = 25,
      offset: Long = 0
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Membership]]

    def getByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Membership]

    def post(
      membershipForm: io.flow.authorization.v0.models.MembershipForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Membership]

    def deleteByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  trait Roles {
    def get(
      guid: _root_.scala.Option[_root_.java.util.UUID] = None,
      guids: _root_.scala.Option[Seq[_root_.java.util.UUID]] = None,
      name: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.authorization.v0.models.Role]]

    def getByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Role]

    def post(
      roleForm: io.flow.authorization.v0.models.RoleForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.authorization.v0.models.Role]

    def deleteByGuid(
      guid: _root_.java.util.UUID
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import io.flow.authorization.v0.models.json._
    import io.flow.common.v0.models.json._

    case class ErrorsResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val errors = _root_.io.flow.authorization.v0.Client.parseJson("Seq[io.flow.common.v0.models.Error]", response, _.validate[Seq[io.flow.common.v0.models.Error]])
    }

    case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends Exception(s"HTTP $responseCode: $message")

  }

}
