/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.0.15
 * apibuilder 0.14.3 app.apibuilder.io/flow/apibuilder-api-mocker/0.0.15/play_2_6_client
 */
package io.flow.apibuilder.api.mocker.v0.models {

  final case class MockApi(
    request: io.flow.apibuilder.api.mocker.v0.models.MockApiRequest,
    response: io.flow.apibuilder.api.mocker.v0.models.MockApiResponse
  )

  final case class MockApiRequest(
    method: String,
    url: String
  )

  final case class MockApiResponse(
    httpStatusCode: Int,
    body: _root_.scala.Option[_root_.play.api.libs.json.JsValue] = None,
    contentType: String = "application/json"
  )

  final case class MockableApi(
    method: String,
    url: String,
    child: _root_.scala.Option[io.flow.apibuilder.api.mocker.v0.models.MockableApi] = None
  )

}

package io.flow.apibuilder.api.mocker.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.apibuilder.api.mocker.v0.models.json._

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

    private[v0] implicit val jsonReadsJodaLocalDate = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateParser
      dateParser.parseLocalDate(str)
    }

    private[v0] implicit val jsonWritesJodaLocalDate = new Writes[org.joda.time.LocalDate] {
      def writes(x: org.joda.time.LocalDate) = {
        import org.joda.time.format.ISODateTimeFormat.date
        val str = date.print(x)
        JsString(str)
      }
    }

    implicit def jsonReadsApibuilderApiMockerMockApi: play.api.libs.json.Reads[MockApi] = {
      (
        (__ \ "request").read[io.flow.apibuilder.api.mocker.v0.models.MockApiRequest] and
        (__ \ "response").read[io.flow.apibuilder.api.mocker.v0.models.MockApiResponse]
      )(MockApi.apply _)
    }

    def jsObjectMockApi(obj: io.flow.apibuilder.api.mocker.v0.models.MockApi): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "request" -> jsObjectMockApiRequest(obj.request),
        "response" -> jsObjectMockApiResponse(obj.response)
      )
    }

    implicit def jsonWritesApibuilderApiMockerMockApi: play.api.libs.json.Writes[MockApi] = {
      new play.api.libs.json.Writes[io.flow.apibuilder.api.mocker.v0.models.MockApi] {
        def writes(obj: io.flow.apibuilder.api.mocker.v0.models.MockApi) = {
          jsObjectMockApi(obj)
        }
      }
    }

    implicit def jsonReadsApibuilderApiMockerMockApiRequest: play.api.libs.json.Reads[MockApiRequest] = {
      (
        (__ \ "method").read[String] and
        (__ \ "url").read[String]
      )(MockApiRequest.apply _)
    }

    def jsObjectMockApiRequest(obj: io.flow.apibuilder.api.mocker.v0.models.MockApiRequest): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "method" -> play.api.libs.json.JsString(obj.method),
        "url" -> play.api.libs.json.JsString(obj.url)
      )
    }

    implicit def jsonWritesApibuilderApiMockerMockApiRequest: play.api.libs.json.Writes[MockApiRequest] = {
      new play.api.libs.json.Writes[io.flow.apibuilder.api.mocker.v0.models.MockApiRequest] {
        def writes(obj: io.flow.apibuilder.api.mocker.v0.models.MockApiRequest) = {
          jsObjectMockApiRequest(obj)
        }
      }
    }

    implicit def jsonReadsApibuilderApiMockerMockApiResponse: play.api.libs.json.Reads[MockApiResponse] = {
      (
        (__ \ "http_status_code").read[Int] and
        (__ \ "body").readNullable[_root_.play.api.libs.json.JsValue] and
        (__ \ "content_type").read[String]
      )(MockApiResponse.apply _)
    }

    def jsObjectMockApiResponse(obj: io.flow.apibuilder.api.mocker.v0.models.MockApiResponse): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "http_status_code" -> play.api.libs.json.JsNumber(obj.httpStatusCode),
        "content_type" -> play.api.libs.json.JsString(obj.contentType)
      ) ++ (obj.body match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("body" -> x)
      })
    }

    implicit def jsonWritesApibuilderApiMockerMockApiResponse: play.api.libs.json.Writes[MockApiResponse] = {
      new play.api.libs.json.Writes[io.flow.apibuilder.api.mocker.v0.models.MockApiResponse] {
        def writes(obj: io.flow.apibuilder.api.mocker.v0.models.MockApiResponse) = {
          jsObjectMockApiResponse(obj)
        }
      }
    }

    implicit def jsonReadsApibuilderApiMockerMockableApi: play.api.libs.json.Reads[MockableApi] = {
      (
        (__ \ "method").read[String] and
        (__ \ "url").read[String] and
        (__ \ "child").lazyReadNullable(play.api.libs.json.Reads.of[io.flow.apibuilder.api.mocker.v0.models.MockableApi])
      )(MockableApi.apply _)
    }

    def jsObjectMockableApi(obj: io.flow.apibuilder.api.mocker.v0.models.MockableApi): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "method" -> play.api.libs.json.JsString(obj.method),
        "url" -> play.api.libs.json.JsString(obj.url)
      ) ++ (obj.child match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("child" -> jsObjectMockableApi(x))
      })
    }

    implicit def jsonWritesApibuilderApiMockerMockableApi: play.api.libs.json.Writes[MockableApi] = {
      new play.api.libs.json.Writes[io.flow.apibuilder.api.mocker.v0.models.MockableApi] {
        def writes(obj: io.flow.apibuilder.api.mocker.v0.models.MockableApi) = {
          jsObjectMockableApi(obj)
        }
      }
    }
  }
}

package io.flow.apibuilder.api.mocker.v0 {

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
      import org.joda.time.{format, DateTime, LocalDate}

      val dateTimeIso8601: ApibuilderTypeConverter[DateTime] = new ApibuilderTypeConverter[DateTime] {
        override def convert(value: String): DateTime = format.ISODateTimeFormat.dateTimeParser.parseDateTime(value)
        override def convert(value: DateTime): String = format.ISODateTimeFormat.dateTime.print(value)
        override def example: DateTime = DateTime.now
      }

      val dateIso8601: ApibuilderTypeConverter[LocalDate] = new ApibuilderTypeConverter[LocalDate] {
        override def convert(value: String): LocalDate = format.ISODateTimeFormat.yearMonthDay.parseLocalDate(value)
        override def convert(value: LocalDate): String = value.toString
        override def example: LocalDate = LocalDate.now
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


package io.flow.apibuilder.api.mocker.v0 {

  object Constants {

    val Namespace = "io.flow.apibuilder.api.mocker.v0"
    val UserAgent = "apibuilder 0.14.3 app.apibuilder.io/flow/apibuilder-api-mocker/0.0.15/play_2_6_client"
    val Version = "0.0.15"
    val VersionMajor = 0

  }

  class Client(
    ws: play.api.libs.ws.WSClient,
    val baseUrl: String,
    auth: scala.Option[io.flow.apibuilder.api.mocker.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import io.flow.apibuilder.api.mocker.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.apibuilder.api.mocker.v0.Client")

    logger.info(s"Initializing io.flow.apibuilder.api.mocker.v0.Client for url $baseUrl")





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
      auth.fold(logger.info(s"curl -X $method $url")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' $url")
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
          throw io.flow.apibuilder.api.mocker.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
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

    }

  }



  package errors {

    final case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

}