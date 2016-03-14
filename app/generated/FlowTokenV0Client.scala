/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.4
 * apidoc:0.11.19 http://www.apidoc.me/flow/token/0.0.4/play_2_4_client
 */
package io.flow.token.v0.models {

  case class Token(
    user: io.flow.common.v0.models.UserReference
  )

  /**
   * Used to create a new token for the specified user.
   */
  case class TokenForm(
    userId: String,
    description: _root_.scala.Option[String] = None
  )

}

package io.flow.token.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.common.v0.models.json._
    import io.flow.token.v0.models.json._

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

    implicit def jsonReadsTokenToken: play.api.libs.json.Reads[Token] = {
      (__ \ "user").read[io.flow.common.v0.models.UserReference].map { x => new Token(user = x) }
    }

    def jsObjectToken(obj: io.flow.token.v0.models.Token) = {
      play.api.libs.json.Json.obj(
        "user" -> io.flow.common.v0.models.json.jsObjectUserReference(obj.user)
      )
    }

    implicit def jsonWritesTokenToken: play.api.libs.json.Writes[Token] = {
      new play.api.libs.json.Writes[io.flow.token.v0.models.Token] {
        def writes(obj: io.flow.token.v0.models.Token) = {
          jsObjectToken(obj)
        }
      }
    }

    implicit def jsonReadsTokenTokenForm: play.api.libs.json.Reads[TokenForm] = {
      (
        (__ \ "user_id").read[String] and
        (__ \ "description").readNullable[String]
      )(TokenForm.apply _)
    }

    def jsObjectTokenForm(obj: io.flow.token.v0.models.TokenForm) = {
      play.api.libs.json.Json.obj(
        "user_id" -> play.api.libs.json.JsString(obj.userId)
      ) ++ (obj.description match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("description" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesTokenTokenForm: play.api.libs.json.Writes[TokenForm] = {
      new play.api.libs.json.Writes[io.flow.token.v0.models.TokenForm] {
        def writes(obj: io.flow.token.v0.models.TokenForm) = {
          jsObjectTokenForm(obj)
        }
      }
    }
  }
}

package io.flow.token.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}
    import org.joda.time.{DateTime, LocalDate}
    import org.joda.time.format.ISODateTimeFormat
    import io.flow.token.v0.models._

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



  }

}


package io.flow.token.v0 {

  object Constants {

    val BaseUrl = "https://token.api.flow.io"
    val Namespace = "io.flow.token.v0"
    val UserAgent = "apidoc:0.11.19 http://www.apidoc.me/flow/token/0.0.4/play_2_4_client"
    val Version = "0.0.4"
    val VersionMajor = 0

  }

  class Client(
    apiUrl: String = "https://token.api.flow.io",
    auth: scala.Option[io.flow.token.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import io.flow.common.v0.models.json._
    import io.flow.token.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.token.v0.Client")

    logger.info(s"Initializing io.flow.token.v0.Client for url $apiUrl")

    def healthchecks: Healthchecks = Healthchecks

    def tokens: Tokens = Tokens

    object Healthchecks extends Healthchecks {
      override def getHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = {
        _executeRequest("GET", s"/_internal_/healthcheck").map {
          case r if r.status == 200 => _root_.io.flow.token.v0.Client.parseJson("io.flow.common.v0.models.Healthcheck", r, _.validate[io.flow.common.v0.models.Healthcheck])
          case r => throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }
    }

    object Tokens extends Tokens {
      override def get(
        token: Seq[String]
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = {
        val queryParameters = token.map("token" -> _)

        _executeRequest("GET", s"/tokens", queryParameters = queryParameters).map {
          case r if r.status == 200 => _root_.io.flow.token.v0.Client.parseJson("Seq[io.flow.token.v0.models.Token]", r, _.validate[Seq[io.flow.token.v0.models.Token]])
          case r => throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200")
        }
      }

      override def getByToken(
        token: String
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = {
        _executeRequest("GET", s"/tokens/${play.utils.UriEncoding.encodePathSegment(token, "UTF-8")}").map {
          case r if r.status == 200 => _root_.io.flow.token.v0.Client.parseJson("io.flow.token.v0.models.Token", r, _.validate[io.flow.token.v0.models.Token])
          case r if r.status == 404 => throw new io.flow.token.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 404")
        }
      }

      override def post(
        tokenForm: io.flow.token.v0.models.TokenForm
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = {
        val payload = play.api.libs.json.Json.toJson(tokenForm)

        _executeRequest("POST", s"/tokens", body = Some(payload)).map {
          case r if r.status == 200 => _root_.io.flow.token.v0.Client.parseJson("Seq[io.flow.token.v0.models.Token]", r, _.validate[Seq[io.flow.token.v0.models.Token]])
          case r if r.status == 401 => throw new io.flow.token.v0.errors.UnitResponse(r.status)
          case r if r.status == 422 => throw new io.flow.token.v0.errors.ErrorsResponse(r)
          case r => throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401, 422")
        }
      }

      override def deleteByToken(
        token: String
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = {
        _executeRequest("DELETE", s"/tokens/${play.utils.UriEncoding.encodePathSegment(token, "UTF-8")}").map {
          case r if r.status == 204 => ()
          case r if r.status == 404 => throw new io.flow.token.v0.errors.UnitResponse(r.status)
          case r => throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 204, 404")
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
          throw new io.flow.token.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
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
      def healthchecks: io.flow.token.v0.Healthchecks
      def tokens: io.flow.token.v0.Tokens
    }

  }

  trait Healthchecks {
    def getHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck]
  }

  trait Tokens {
    /**
     * Get user reference by token
     */
    def get(
      token: Seq[String]
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]]

    /**
     * Get the user for this specified token
     */
    def getByToken(
      token: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token]

    /**
     * Create a user token
     */
    def post(
      tokenForm: io.flow.token.v0.models.TokenForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]]

    def deleteByToken(
      token: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit]
  }

  package errors {

    import io.flow.common.v0.models.json._
    import io.flow.token.v0.models.json._

    case class ErrorsResponse(
      response: play.api.libs.ws.WSResponse,
      message: Option[String] = None
    ) extends Exception(message.getOrElse(response.status + ": " + response.body)){
      lazy val errors = _root_.io.flow.token.v0.Client.parseJson("Seq[io.flow.common.v0.models.Error]", response, _.validate[Seq[io.flow.common.v0.models.Error]])
    }

    case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends Exception(s"HTTP $responseCode: $message")

  }

}