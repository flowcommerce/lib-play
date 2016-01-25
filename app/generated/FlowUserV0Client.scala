/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.8
 * apidoc:0.11.6 http://www.apidoc.me/flow/user/0.0.8/play_2_4_client
 */
package io.flow.user.v0.models {

  case class NameForm(
    first: _root_.scala.Option[String] = None,
    last: _root_.scala.Option[String] = None
  )

  case class UserForm(
    email: _root_.scala.Option[String] = None,
    name: _root_.scala.Option[io.flow.user.v0.models.NameForm] = None,
    avatarUrl: _root_.scala.Option[String] = None
  )

  case class UserVersion(
    id: String,
    timestamp: _root_.org.joda.time.DateTime,
    `type`: io.flow.common.v0.models.ChangeType,
    user: io.flow.common.v0.models.User
  )

  sealed trait System

  object System {

    case object Github extends System { override def toString = "github" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends System

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Github)

    private[this]
    val byName = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): System = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[System] = byName.get(value.toLowerCase)

  }

}

package io.flow.user.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.common.v0.models.json._
    import io.flow.user.v0.models.json._

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

    implicit val jsonReadsUserSystem = new play.api.libs.json.Reads[io.flow.user.v0.models.System] {
      def reads(js: play.api.libs.json.JsValue): play.api.libs.json.JsResult[io.flow.user.v0.models.System] = {
        js match {
          case v: play.api.libs.json.JsString => play.api.libs.json.JsSuccess(io.flow.user.v0.models.System(v.value))
          case _ => {
            (js \ "value").validate[String] match {
              case play.api.libs.json.JsSuccess(v, _) => play.api.libs.json.JsSuccess(io.flow.user.v0.models.System(v))
              case err: play.api.libs.json.JsError => err
            }
          }
        }
      }
    }

    def jsonWritesUserSystem(obj: io.flow.user.v0.models.System) = {
      play.api.libs.json.JsString(obj.toString)
    }

    def jsObjectSystem(obj: io.flow.user.v0.models.System) = {
      play.api.libs.json.Json.obj("value" -> play.api.libs.json.JsString(obj.toString))
    }

    implicit def jsonWritesUserSystem: play.api.libs.json.Writes[System] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.System] {
        def writes(obj: io.flow.user.v0.models.System) = {
          jsonWritesUserSystem(obj)
        }
      }
    }

    implicit def jsonReadsUserNameForm: play.api.libs.json.Reads[NameForm] = {
      (
        (__ \ "first").readNullable[String] and
        (__ \ "last").readNullable[String]
      )(NameForm.apply _)
    }

    def jsObjectNameForm(obj: io.flow.user.v0.models.NameForm) = {
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

    implicit def jsonReadsUserUserForm: play.api.libs.json.Reads[UserForm] = {
      (
        (__ \ "email").readNullable[String] and
        (__ \ "name").readNullable[io.flow.user.v0.models.NameForm] and
        (__ \ "avatar_url").readNullable[String]
      )(UserForm.apply _)
    }

    def jsObjectUserForm(obj: io.flow.user.v0.models.UserForm) = {
      (obj.email match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("email" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.name match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("name" -> jsObjectNameForm(x))
      }) ++
      (obj.avatarUrl match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("avatar_url" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesUserUserForm: play.api.libs.json.Writes[UserForm] = {
      new play.api.libs.json.Writes[io.flow.user.v0.models.UserForm] {
        def writes(obj: io.flow.user.v0.models.UserForm) = {
          jsObjectUserForm(obj)
        }
      }
    }

    implicit def jsonReadsUserUserVersion: play.api.libs.json.Reads[UserVersion] = {
      (
        (__ \ "id").read[String] and
        (__ \ "timestamp").read[_root_.org.joda.time.DateTime] and
        (__ \ "type").read[io.flow.common.v0.models.ChangeType] and
        (__ \ "user").read[io.flow.common.v0.models.User]
      )(UserVersion.apply _)
    }

    def jsObjectUserVersion(obj: io.flow.user.v0.models.UserVersion) = {
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
    import org.joda.time.{DateTime, LocalDate}
    import org.joda.time.format.ISODateTimeFormat
    import io.flow.user.v0.models._

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

    // Enum: System
    private[this] val enumSystemNotFound = (key: String, e: Exception) => s"Unrecognized $key, should be one of ${io.flow.user.v0.models.System.all.mkString(", ")}"

    implicit val pathBindableEnumSystem = new PathBindable.Parsing[io.flow.user.v0.models.System] (
      System.fromString(_).get, _.toString, enumSystemNotFound
    )

    implicit val queryStringBindableEnumSystem = new QueryStringBindable.Parsing[io.flow.user.v0.models.System](
      System.fromString(_).get, _.toString, enumSystemNotFound
    )

  }

}


package io.flow.user.v0 {

  object Constants {

    val Namespace = "io.flow.user.v0"
    val UserAgent = "apidoc:0.11.6 http://www.apidoc.me/flow/user/0.0.8/play_2_4_client"
    val Version = "0.0.8"
    val VersionMajor = 0

  }

  class Client(
    apiUrl: String,
    auth: scala.Option[io.flow.user.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import io.flow.common.v0.models.json._
    import io.flow.user.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.user.v0.Client")

    logger.info(s"Initializing io.flow.user.v0.Client for url $apiUrl")

    def healthchecks: Healthchecks = Healthchecks

    def users: Users = Users

    object Healthchecks extends Healthchecks {
      override def getHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = {
        _executeRequest("GET", s"/_internal_/healthcheck").map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.Healthcheck", r, _.validate[io.flow.common.v0.models.Healthcheck])
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }
    }

    object Users extends Users {
      override def get(
        id: _root_.scala.Option[Seq[String]] = None,
        email: _root_.scala.Option[String] = None,
        token: _root_.scala.Option[String] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "-created_at"
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]] = {
        val queryParameters = Seq(
          email.map("email" -> _),
          token.map("token" -> _),
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _)

        _executeRequest("GET", s"/users", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("Seq[io.flow.common.v0.models.User]", r, _.validate[Seq[io.flow.common.v0.models.User]])
          case r if r.status == 401 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401")
        }
      }

      override def getVersions(
        id: _root_.scala.Option[Seq[String]] = None,
        userId: _root_.scala.Option[Seq[String]] = None,
        limit: Long = 25,
        offset: Long = 0,
        sort: String = "journal_timestamp"
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.user.v0.models.UserVersion]] = {
        val queryParameters = Seq(
          Some("limit" -> limit.toString),
          Some("offset" -> offset.toString),
          Some("sort" -> sort)
        ).flatten ++
          id.getOrElse(Nil).map("id" -> _) ++
          userId.getOrElse(Nil).map("user_id" -> _)

        _executeRequest("GET", s"/users/versions", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("Seq[io.flow.user.v0.models.UserVersion]", r, _.validate[Seq[io.flow.user.v0.models.UserVersion]])
          case r if r.status == 401 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401")
        }
      }

      override def getTokensByToken(
        token: String
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        _executeRequest("GET", s"/users/tokens/${play.utils.UriEncoding.encodePathSegment(token, "UTF-8")}").map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def getById(
        id: String
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        _executeRequest("GET", s"/users/${play.utils.UriEncoding.encodePathSegment(id, "UTF-8")}").map {
          case r if r.status == 200 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 404 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 404")
        }
      }

      override def post(
        userForm: io.flow.user.v0.models.UserForm
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = {
        val payload = play.api.libs.json.Json.toJson(userForm)

        _executeRequest("POST", s"/users", body = Some(payload)).map {
          case r if r.status == 201 => _root_.io.flow.user.v0.Client.parseJson("io.flow.common.v0.models.User", r, _.validate[io.flow.common.v0.models.User])
          case r if r.status == 401 => throw new io.flow.user.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw new io.flow.user.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 201, 401, 422")
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
          throw new io.flow.user.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization
  object Authorization {
    case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def healthchecks: io.flow.user.v0.Healthchecks
      def users: io.flow.user.v0.Users
    }

  }

  trait Healthchecks {
    def getHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck]
  }

  trait Users {
    /**
     * Search users. Always paginated.
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      email: _root_.scala.Option[String] = None,
      token: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at"
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]]

    /**
     * Provides visibility into recent changes of each object, including deletion
     */
    def getVersions(
      id: _root_.scala.Option[Seq[String]] = None,
      userId: _root_.scala.Option[Seq[String]] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "journal_timestamp"
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.user.v0.models.UserVersion]]

    /**
     * Lookup a user by token. This is publicly available method given that the tokens
     * themselves are secure random strings.
     */
    def getTokensByToken(
      token: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Returns information about a specific user.
     */
    def getById(
      id: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]

    /**
     * Create a new user.
     */
    def post(
      userForm: io.flow.user.v0.models.UserForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User]
  }

  package errors {

    import io.flow.common.v0.models.json._
    import io.flow.user.v0.models.json._

    case class ErrorsResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val errors = _root_.io.flow.user.v0.Client.parseJson("Seq[io.flow.common.v0.models.Error]", response, _.validate[Seq[io.flow.common.v0.models.Error]])
    }

    case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends Exception(s"HTTP $responseCode: $message")

  }

}