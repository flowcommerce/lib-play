/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.7.94
 * apibuilder 0.14.89 app.apibuilder.io/flow/user/latest/play_2_6_client
 */
package io.flow.user.v0.models {

  final case class AuthenticationForm(
    email: String,
    password: String
  )

  /**
   * Represents the successful response of an email verification token. We return the
   * email address in this case to allow the UI to display which email address was
   * verified.
   */
  final case class EmailVerification(
    email: String
  )

  final case class NameForm(
    first: _root_.scala.Option[String] = None,
    last: _root_.scala.Option[String] = None
  )

  /**
   * @param current The current valid account password
   * @param `new` The new password
   */
  final case class PasswordChangeForm(
    current: String,
    `new`: String
  )

  /**
   * @param token The token for the password reset request
   * @param password The new password
   */
  final case class PasswordResetForm(
    token: String,
    password: String
  )

  final case class PasswordResetRequestForm(
    email: String
  )

  final case class UserForm(
    email: _root_.scala.Option[String] = None,
    password: _root_.scala.Option[String] = None,
    name: _root_.scala.Option[io.flow.user.v0.models.NameForm] = None
  )

  final case class UserPutForm(
    email: _root_.scala.Option[String] = None,
    name: _root_.scala.Option[io.flow.user.v0.models.NameForm] = None
  )

  final case class UserVersion(
    id: String,
    timestamp: _root_.org.joda.time.DateTime,
    `type`: io.flow.common.v0.models.ChangeType,
    user: io.flow.common.v0.models.User
  )

}

package io.flow.user.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.common.v0.models.json._
    import io.flow.error.v0.models.json._
    import io.flow.permission.v0.models.json._
    import io.flow.user.v0.models.json._

    private[v0] implicit val jsonReadsUUID = __.read[String].map { str =>
      _root_.java.util.UUID.fromString(str)
    }

    private[v0] implicit val jsonWritesUUID = new Writes[_root_.java.util.UUID] {
      def writes(x: _root_.java.util.UUID) = JsString(x.toString)
    }

    private[v0] implicit val jsonReadsJodaDateTime = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(str)
    }

    private[v0] implicit val jsonWritesJodaDateTime = new Writes[_root_.org.joda.time.DateTime] {
      def writes(x: _root_.org.joda.time.DateTime) = {
        JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(x))
      }
    }

    private[v0] implicit val jsonReadsJodaLocalDate = __.read[String].map { str =>
      _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(str)
    }

    private[v0] implicit val jsonWritesJodaLocalDate = new Writes[_root_.org.joda.time.LocalDate] {
      def writes(x: _root_.org.joda.time.LocalDate) = {
        JsString(_root_.org.joda.time.format.ISODateTimeFormat.date.print(x))
      }
    }

    implicit def jsonReadsUserAuthenticationForm: play.api.libs.json.Reads[AuthenticationForm] = {
      for {
        email <- (__ \ "email").read[String]
        password <- (__ \ "password").read[String]
      } yield AuthenticationForm(email, password)
    }

    def jsObjectAuthenticationForm(obj: io.flow.user.v0.models.AuthenticationForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "email" -> play.api.libs.json.JsString(obj.email),
        "password" -> play.api.libs.json.JsString(obj.password)
      )
    }

    implicit def jsonWritesUserAuthenticationForm: play.api.libs.json.Writes[AuthenticationForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.AuthenticationForm] {
        def writes(obj: io.flow.user.v0.models.AuthenticationForm) = {
          jsObjectAuthenticationForm(obj)
        }
      }
    }

    implicit def jsonReadsUserEmailVerification: play.api.libs.json.Reads[EmailVerification] = {
      (__ \ "email").read[String].map { x => new EmailVerification(email = x) }
    }

    def jsObjectEmailVerification(obj: io.flow.user.v0.models.EmailVerification): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "email" -> play.api.libs.json.JsString(obj.email)
      )
    }

    implicit def jsonWritesUserEmailVerification: play.api.libs.json.Writes[EmailVerification] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.EmailVerification] {
        def writes(obj: io.flow.user.v0.models.EmailVerification) = {
          jsObjectEmailVerification(obj)
        }
      }
    }

    implicit def jsonReadsUserNameForm: play.api.libs.json.Reads[NameForm] = {
      for {
        first <- (__ \ "first").readNullable[String]
        last <- (__ \ "last").readNullable[String]
      } yield NameForm(first, last)
    }

    def jsObjectNameForm(obj: io.flow.user.v0.models.NameForm): play.api.libs.json.JsObject = {
      (obj.first match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("first" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.last match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("last" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesUserNameForm: play.api.libs.json.Writes[NameForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.NameForm] {
        def writes(obj: io.flow.user.v0.models.NameForm) = {
          jsObjectNameForm(obj)
        }
      }
    }

    implicit def jsonReadsUserPasswordChangeForm: play.api.libs.json.Reads[PasswordChangeForm] = {
      for {
        current <- (__ \ "current").read[String]
        `new` <- (__ \ "new").read[String]
      } yield PasswordChangeForm(current, `new`)
    }

    def jsObjectPasswordChangeForm(obj: io.flow.user.v0.models.PasswordChangeForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "current" -> play.api.libs.json.JsString(obj.current),
        "new" -> play.api.libs.json.JsString(obj.`new`)
      )
    }

    implicit def jsonWritesUserPasswordChangeForm: play.api.libs.json.Writes[PasswordChangeForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.PasswordChangeForm] {
        def writes(obj: io.flow.user.v0.models.PasswordChangeForm) = {
          jsObjectPasswordChangeForm(obj)
        }
      }
    }

    implicit def jsonReadsUserPasswordResetForm: play.api.libs.json.Reads[PasswordResetForm] = {
      for {
        token <- (__ \ "token").read[String]
        password <- (__ \ "password").read[String]
      } yield PasswordResetForm(token, password)
    }

    def jsObjectPasswordResetForm(obj: io.flow.user.v0.models.PasswordResetForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "token" -> play.api.libs.json.JsString(obj.token),
        "password" -> play.api.libs.json.JsString(obj.password)
      )
    }

    implicit def jsonWritesUserPasswordResetForm: play.api.libs.json.Writes[PasswordResetForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.PasswordResetForm] {
        def writes(obj: io.flow.user.v0.models.PasswordResetForm) = {
          jsObjectPasswordResetForm(obj)
        }
      }
    }

    implicit def jsonReadsUserPasswordResetRequestForm: play.api.libs.json.Reads[PasswordResetRequestForm] = {
      (__ \ "email").read[String].map { x => new PasswordResetRequestForm(email = x) }
    }

    def jsObjectPasswordResetRequestForm(obj: io.flow.user.v0.models.PasswordResetRequestForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "email" -> play.api.libs.json.JsString(obj.email)
      )
    }

    implicit def jsonWritesUserPasswordResetRequestForm: play.api.libs.json.Writes[PasswordResetRequestForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.PasswordResetRequestForm] {
        def writes(obj: io.flow.user.v0.models.PasswordResetRequestForm) = {
          jsObjectPasswordResetRequestForm(obj)
        }
      }
    }

    implicit def jsonReadsUserUserForm: play.api.libs.json.Reads[UserForm] = {
      for {
        email <- (__ \ "email").readNullable[String]
        password <- (__ \ "password").readNullable[String]
        name <- (__ \ "name").readNullable[io.flow.user.v0.models.NameForm]
      } yield UserForm(email, password, name)
    }

    def jsObjectUserForm(obj: io.flow.user.v0.models.UserForm): play.api.libs.json.JsObject = {
      (obj.email match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("email" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.password match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("password" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.name match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("name" -> jsObjectNameForm(x))
      })
    }

    implicit def jsonWritesUserUserForm: play.api.libs.json.Writes[UserForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.UserForm] {
        def writes(obj: io.flow.user.v0.models.UserForm) = {
          jsObjectUserForm(obj)
        }
      }
    }

    implicit def jsonReadsUserUserPutForm: play.api.libs.json.Reads[UserPutForm] = {
      for {
        email <- (__ \ "email").readNullable[String]
        name <- (__ \ "name").readNullable[io.flow.user.v0.models.NameForm]
      } yield UserPutForm(email, name)
    }

    def jsObjectUserPutForm(obj: io.flow.user.v0.models.UserPutForm): play.api.libs.json.JsObject = {
      (obj.email match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("email" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.name match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("name" -> jsObjectNameForm(x))
      })
    }

    implicit def jsonWritesUserUserPutForm: play.api.libs.json.Writes[UserPutForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.UserPutForm] {
        def writes(obj: io.flow.user.v0.models.UserPutForm) = {
          jsObjectUserPutForm(obj)
        }
      }
    }

    implicit def jsonReadsUserUserVersion: play.api.libs.json.Reads[UserVersion] = {
      for {
        id <- (__ \ "id").read[String]
        timestamp <- (__ \ "timestamp").read[_root_.org.joda.time.DateTime]
        `type` <- (__ \ "type").read[io.flow.common.v0.models.ChangeType]
        user <- (__ \ "user").read[io.flow.common.v0.models.User]
      } yield UserVersion(id, timestamp, `type`, user)
    }

    def jsObjectUserVersion(obj: io.flow.user.v0.models.UserVersion): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "id" -> play.api.libs.json.JsString(obj.id),
        "timestamp" -> play.api.libs.json.JsString(_root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(obj.timestamp)),
        "type" -> play.api.libs.json.JsString(obj.`type`.toString),
        "user" -> io.flow.common.v0.models.json.jsObjectUser(obj.user)
      )
    }

    implicit def jsonWritesUserUserVersion: play.api.libs.json.Writes[UserVersion] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.UserVersion] {
        def writes(obj: io.flow.user.v0.models.UserVersion) = {
          jsObjectUserVersion(obj)
        }
      }
    }
  }
}

package io.flow.user.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}

    // import models directly for backwards compatibility with prior versions of the generator
    import Core._

    object Core {
      implicit def pathBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.DateTime] = ApibuilderPathBindable(ApibuilderTypes.dateTimeIso8601)
      implicit def queryStringBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.DateTime] = ApibuilderQueryStringBindable(ApibuilderTypes.dateTimeIso8601)

      implicit def pathBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.LocalDate] = ApibuilderPathBindable(ApibuilderTypes.dateIso8601)
      implicit def queryStringBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.LocalDate] = ApibuilderQueryStringBindable(ApibuilderTypes.dateIso8601)
    }

    trait ApibuilderTypeConverter[T] {

      def convert(value: String): T

      def convert(value: T): String

      def example: T

      def validValues: Seq[T] = Nil

      def errorMessage(key: String, value: String, ex: java.lang.Exception): String = {
        val base = s"Invalid value '$value' for parameter '$key'. "
        validValues.toList match {
          case Nil => base + "Ex: " + convert(example)
          case values => base + ". Valid values are: " + values.mkString("'", "', '", "'")
        }
      }
    }

    object ApibuilderTypes {
      val dateTimeIso8601: ApibuilderTypeConverter[_root_.org.joda.time.DateTime] = new ApibuilderTypeConverter[_root_.org.joda.time.DateTime] {
        override def convert(value: String): _root_.org.joda.time.DateTime = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseDateTime(value)
        override def convert(value: _root_.org.joda.time.DateTime): String = _root_.org.joda.time.format.ISODateTimeFormat.dateTime.print(value)
        override def example: _root_.org.joda.time.DateTime = _root_.org.joda.time.DateTime.now
      }

      val dateIso8601: ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] = new ApibuilderTypeConverter[_root_.org.joda.time.LocalDate] {
        override def convert(value: String): _root_.org.joda.time.LocalDate = _root_.org.joda.time.format.ISODateTimeFormat.dateTimeParser.parseLocalDate(value)
        override def convert(value: _root_.org.joda.time.LocalDate): String = _root_.org.joda.time.format.ISODateTimeFormat.date.print(value)
        override def example: _root_.org.joda.time.LocalDate = _root_.org.joda.time.LocalDate.now
      }
    }

    final case class ApibuilderQueryStringBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends QueryStringBindable[T] {

      override def bind(key: String, params: Map[String, Seq[String]]): _root_.scala.Option[_root_.scala.Either[String, T]] = {
        params.getOrElse(key, Nil).headOption.map { v =>
          try {
            Right(
              converters.convert(v)
            )
          } catch {
            case ex: java.lang.Exception => Left(
              converters.errorMessage(key, v, ex)
            )
          }
        }
      }

      override def unbind(key: String, value: T): String = {
        s"$key=${converters.convert(value)}"
      }
    }

    final case class ApibuilderPathBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends PathBindable[T] {

      override def bind(key: String, value: String): _root_.scala.Either[String, T] = {
        try {
          Right(
            converters.convert(value)
          )
        } catch {
          case ex: java.lang.Exception => Left(
            converters.errorMessage(key, value, ex)
          )
        }
      }

      override def unbind(key: String, value: T): String = {
        converters.convert(value)
      }
    }

  }

}


package io.flow.user.v0 {

  object Constants {

    val Namespace = "io.flow.user.v0"
    val UserAgent = "apibuilder 0.14.89 app.apibuilder.io/flow/user/latest/play_2_6_client"
    val Version = "0.7.94"
    val VersionMajor = 0

  }

  class Client(
    ws: play.api.libs.ws.WSClient,
    val baseUrl: String,
    auth: scala.Option[io.flow.user.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import io.flow.common.v0.models.json._
    import io.flow.error.v0.models.json._
    import io.flow.permission.v0.models.json._
    import io.flow.user.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.user.v0.Client")

    logger.info(s"Initializing io.flow.user.v0.Client for url $baseUrl")

    def emailVerifications: EmailVerifications = EmailVerifications

    def passwordResetForms: PasswordResetForms = PasswordResetForms

    def users: Users = Users

    object EmailVerifications extends EmailVerifications {
      override def postByToken(
        token: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.user.v0.models.EmailVerification] = {
        _executeRequest("POST", s"/users/emails/verifications/${play.utils.UriEncoding.encodePathSegment(token, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.user.v0.models.EmailVerification", r, _.validate[io.flow.user.v0.models.EmailVerification])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 422")
        }
      }
    }

    object PasswordResetForms extends PasswordResetForms {
      override def postResets(
        passwordResetRequestForm: io.flow.user.v0.models.PasswordResetRequestForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        val payload = play.api.libs.json.Json.toJson(passwordResetRequestForm)

        _executeRequest("POST", s"/users/passwords/resets", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 204 => ()
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 401, 422")
        }
      }

      override def post(
        passwordResetForm: io.flow.user.v0.models.PasswordResetForm,
        expand: _root_.scala.Option[Seq[String]] = None,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.ExpandableUser] = {
        val payload = play.api.libs.json.Json.toJson(passwordResetForm)

        val queryParameters = expand.getOrElse(Nil).map("expand" -> _)

        _executeRequest("POST", s"/users/passwords", body = Some(payload), queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.ExpandableUser", r, _.validate[io.flow.common.v0.models.ExpandableUser])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }
    }

    object Users extends Users {
      override def get(
        id: _root_.scala.Option[Seq[String]] = None,
        email: _root_.scala.Option[String] = None,
        status: _root_.scala.Option[io.flow.common.v0.models.UserStatus] = None,
        limit: Long = 25L,
        offset: Long = 0L,
        sort: String = "-created_at",
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]] = {
        val queryParameters = Seq(
          email.map("email" -> _),
          status.map("status" -> _.toString),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _)

        _executeRequest("GET", s"/users", queryParameters = queryParameters, requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("Seq[io.flow.common.v0.models.User]", r, _.validate[Seq[io.flow.common.v0.models.User]])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 403 => throw io.flow.user.v0.errors.PermissionErrorResponse(r)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 403, 422")
        }
      }

      override def postAuthenticate(
        authenticationForm: io.flow.user.v0.models.AuthenticationForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        val payload = play.api.libs.json.Json.toJson(authenticationForm)

        _executeRequest("POST", s"/users/authenticate", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404, 422")
        }
      }

      override def getById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        _executeRequest("GET", s"/users/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}", requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 403 => throw io.flow.user.v0.errors.PermissionErrorResponse(r)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 403, 404")
        }
      }

      override def post(
        userForm: io.flow.user.v0.models.UserForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        val payload = play.api.libs.json.Json.toJson(userForm)

        _executeRequest("POST", s"/users", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 201 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 201, 401, 422")
        }
      }

      override def putById(
        id: String,
        userPutForm: io.flow.user.v0.models.UserPutForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        val payload = play.api.libs.json.Json.toJson(userPutForm)

        _executeRequest("PUT", s"/users/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 403 => throw io.flow.user.v0.errors.PermissionErrorResponse(r)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 403, 404, 422")
        }
      }

      override def patchPasswordsById(
        id: String,
        passwordChangeForm: io.flow.user.v0.models.PasswordChangeForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        val payload = play.api.libs.json.Json.toJson(passwordChangeForm)

        _executeRequest("PATCH", s"/users/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}/passwords", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 204 => ()
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 403 => throw io.flow.user.v0.errors.PermissionErrorResponse(r)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 401, 403, 404, 422")
        }
      }

      override def deletePasswordsById(
        id: String,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/users/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}/passwords", requestHeaders = requestHeaders).map {
          case r if r.status == 204 => ()
          case r if r.status == 401 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 403 => throw io.flow.user.v0.errors.PermissionErrorResponse(r)
          case r if r.status == 404 => throw io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw io.flow.user.v0.errors.GenericErrorResponse(r)
          case r => throw io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 401, 403, 404, 422")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {

      val holder = ws.url(baseUrl + path).addHttpHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).addHttpHeaders(defaultHeaders : _*)
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
      auth.fold(logger.info(s"curl -X $method '$url'")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' '$url'")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Nil,
      requestHeaders: Seq[(String, String)] = Nil,
      body: Option[play.api.libs.json.JsValue] = None
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

    /**
     * Adds a Content-Type: application/json header unless the specified requestHeaders
     * already contain a Content-Type header
     */
    def _withJsonContentType(headers: Seq[(String, String)]): Seq[(String, String)] = {
      headers.find { _._1.toUpperCase == "CONTENT-TYPE" } match {
        case None => headers ++ Seq(("Content-Type" -> "application/json; charset=UTF-8"))
        case Some(_) => headers
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
          throw io.flow.user.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization extends _root_.scala.Product with _root_.scala.Serializable
  object Authorization {
    final case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def baseUrl: String
      def emailVerifications: io.flow.user.v0.EmailVerifications
      def passwordResetForms: io.flow.user.v0.PasswordResetForms
      def users: io.flow.user.v0.Users
    }

  }

  trait EmailVerifications {
    /**
     * @param token The unique token sent to the user to verify their email address
     */
    def postByToken(
      token: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.user.v0.models.EmailVerification]
  }

  trait PasswordResetForms {
    def postResets(
      passwordResetRequestForm: io.flow.user.v0.models.PasswordResetRequestForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]

    def post(
      passwordResetForm: io.flow.user.v0.models.PasswordResetForm,
      expand: _root_.scala.Option[Seq[String]] = None,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.ExpandableUser]
  }

  trait Users {
    /**
     * Search users. Must specify an id or email.
     * 
     * @param email Find users with this email address. Case insensitive. Exact match
     * @param status Find users with this status
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      email: _root_.scala.Option[String] = None,
      status: _root_.scala.Option[io.flow.common.v0.models.UserStatus] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]]

    /**
     * Authenticates a user by email / password. Note only users that have a status of
     * active will be authorized.
     */
    def postAuthenticate(
      authenticationForm: io.flow.user.v0.models.AuthenticationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Returns information about a specific user.
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Create a new user. Note that new users will be created with a status of pending
     * and will not be able to authenticate until approved by a member of the Flow
     * team.
     */
    def post(
      userForm: io.flow.user.v0.models.UserForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Update a user.
     */
    def putById(
      id: String,
      userPutForm: io.flow.user.v0.models.UserPutForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Update the password for a user.
     */
    def patchPasswordsById(
      id: String,
      passwordChangeForm: io.flow.user.v0.models.PasswordChangeForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]

    /**
     * Deletes a password for the given user.
     */
    def deletePasswordsById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import io.flow.common.v0.models.json._
    import io.flow.error.v0.models.json._
    import io.flow.permission.v0.models.json._
    import io.flow.user.v0.models.json._

    final case class GenericErrorResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val genericError = _root_.io.flow.user.v0.Client.parseJson("io.flow.error.v0.models.GenericError", response, _.validate[io.flow.error.v0.models.GenericError])
    }

    final case class PermissionErrorResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val permissionError = _root_.io.flow.user.v0.Client.parseJson("io.flow.permission.v0.models.PermissionError", response, _.validate[io.flow.permission.v0.models.PermissionError])
    }

    final case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    final case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

}