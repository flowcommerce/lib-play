package io.flow.play.util

import akka.util.ByteString
import io.flow.apibuilder.api.mocker.v0.models.{MockApi, MockApiRequest, MockApiResponse}
import play.api.http.HttpEntity
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSResponse
import play.api.libs.ws.ahc.cache.{CacheableHttpResponseBodyPart, CacheableHttpResponseHeaders, CacheableHttpResponseStatus}
import play.api.mvc.{Request, ResponseHeader, Result}
import play.shaded.ahc.io.netty.handler.codec.http.DefaultHttpHeaders
import play.shaded.ahc.org.asynchttpclient.Response
import play.shaded.ahc.org.asynchttpclient.uri.Uri

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import io.flow.apibuilder.api.mocker.v0.models.json._

object MockableApiUtil {

  /*
    Examples of adding mocks to generated client request

    // USAGE: SINGLE MOCK
    val response: YourResponseType =
        Await.result(
          identifiedOrgClient(org).foo.post(
            organization = organization,
            number = order.number,

            // For matched request, respond with <mocked> response
            requestHeaders = MatchFlowRequest(PUT, "/:organization/orders/:number/foo/bar") -> RespondWith(OK, io.flow.your.type.v0.mock.Factories.makeYourType())

          ), DefaultDuration)

    // USAGE: MULTIPLE MOCKS
    val response: YourResponseType =
        Await.result(
          identifiedOrgClient(org).foo.post(
            organization = organization,
            number = order.number,

            // For each matched request, respond with <mocked> response
            requestHeaders = Map[MatchRequest, RespondWithJson](
              MatchFlowRequest(PUT, "/:organization/orders/:number/foo/bar") -> RespondWith(OK, io.flow.your.type.v0.mock.Factories.makeYourType()),
              MatchGenericRequest(PUT, "http://external.api.com/external/path") -> RespondWith(OK, SomeExternalApiResponse("test"))
            )

          ), DefaultDuration)
 */

  // naming things
  val `X-Mock-Api-Secret` = "X-Mock-Api-Secret"
  val `X-Mock-Apis` = "X-Mock-Apis"
  val MOCK_API_SECRET = "MOCK_API_SECRET"

  // for use where play request is available; on play controller actions
  def withMockableApis[T](request: Request[T])
    (f: => Future[Result])
    (implicit ec: ExecutionContext): Future[Result] = {
    val headers = request.headers.toMap
    val method = request.method.toUpperCase
    val url = request.uri
    maybeMockApiResponse(method, url, headers)
      .map {
        mockApiResponse =>
          Result(
            header = ResponseHeader(
              status = mockApiResponse.httpStatusCode,
              headers = request.headers.toSimpleMap
            ),
            body = HttpEntity.Strict(
              data = ByteString(mockApiResponse.body.map(jsValue => Json.stringify(jsValue)).getOrElse("").getBytes),
              contentType = mockApiResponse.body.map(_ => mockApiResponse.contentType))
          )
      }.map(Future.successful).getOrElse(f)
  }

  // for use on non-play request
  def withMockableApis(method: String, url: String, requestHeaders: Seq[(String, String)])
    (f: => Future[WSResponse])
    (implicit ec: ExecutionContext): Future[WSResponse] =
      maybeMockApiResponse(method, url, requestHeaders)
        .map {
          mockApiResponse =>
            new AhcWSResponse(new Response.ResponseBuilder()
              .accumulate(new CacheableHttpResponseStatus(Uri.create(url), mockApiResponse.httpStatusCode, "mock-status", "mock-protocols"))
              .accumulate(CacheableHttpResponseHeaders(false, new DefaultHttpHeaders()
                .add("Content-Type", mockApiResponse.contentType)
              ))
              .accumulate(new CacheableHttpResponseBodyPart(mockApiResponse.body.map(Json.stringify).getOrElse("").getBytes(), true))
              .build())
        }.map(Future.successful).getOrElse(f)

  private[this] def maybeMockApiResponse[T]( method: String, url: String, headers: Seq[(String, String)]): Option[MockApiResponse] = {
    for {
      secretFromHeader <- fromHeaders(headers, `X-Mock-Api-Secret`)
      secret <- fromEnvironmentVariable(MOCK_API_SECRET)
      mockApis <- fromJsonHeaders[Seq[MockApi]](headers, `X-Mock-Apis`)
      mockApi <- findMockApi(mockApis, method, url)
      mockApiResponse <- getMockApiResponse(mockApi, secretFromHeader, secret)
    } yield mockApiResponse
  }

  def fromHeaders(requestHeaders: Seq[(String, String)], headerKey: String): Option[String] =
    requestHeaders.find {
      case (key, _) => key == headerKey
    }.map(_._2)

  def fromJsonHeaders[T](requestHeaders: Seq[(String, String)], headerKey: String)(implicit r: Reads[T], m: Manifest[T]): Option[T] =
    requestHeaders.find {
      case (k, _) => k == headerKey
    }.map(_._2)
      .flatMap(
        jsonString =>
          Try(Json.parse(jsonString).as[T]).toOption
      )

  private[this] def fromEnvironmentVariable(name: String): Option[String] = sys.env.get(name)

  private[this]def findMockApi(mockApis: Seq[MockApi], method: String, url: String): Option[MockApi] =
    mockApis.find(mockApi => mockApi.request.url.toUpperCase == url.toUpperCase && mockApi.request.method.toUpperCase == method.toUpperCase)

  private[this]def getMockApiResponse(mockApi: MockApi, secretFromHeader: String, secretFromEnvironmentVariable: String): Option[MockApiResponse] =
    if (secretFromHeader == secretFromEnvironmentVariable) Option(mockApi.response) else None

  implicit def headersToSeq(h: Map[String, Seq[String]]): Seq[(String, String)] =
    h.foldLeft(Seq.empty[(String, String)]) {
      case (acc, (key, values)) => acc ++ values.map(value => (key, value))
    }


  // helpers for consumers
  case class RespondWithJson(status: Int, body: JsValue)
  case class RespondWith[T](status: Int, body: T)
  implicit def toMockedResponse[T](r: RespondWith[T])
    (implicit w: Writes[T], m: Manifest[T]): RespondWithJson = {
    RespondWithJson(r.status, Json.toJson(r.body))
  }
  sealed trait MatchRequest {
    val verb: String
    val url: String
  }
  case class MatchGenericRequest(verb: String, url: String) extends MatchRequest
  case class MatchFlowRequest(verb: String, path: String) extends MatchRequest {
    lazy val baseUrl: String = "https://api.flow.io"
    val url: String = baseUrl + path
  }
  case class MockedApi(req: MatchRequest, res: RespondWithJson)
  implicit def mocksFromMap(m: Map[MatchRequest, RespondWithJson]): Seq[(String, String)] = {
    m.toSeq.map {
      case (req, res) => MockedApi(req, res)
    }
  }
  def serializeMockedApis(mockedApis: Seq[MockedApi]): String = {
    val apis: Seq[MockApi] = mockedApis.map { a =>
      io.flow.apibuilder.api.mocker.v0.models.MockApi(
        io.flow.apibuilder.api.mocker.v0.models.MockApiRequest(
          method = a.req.verb,
          url = a.req.url
        ),
        io.flow.apibuilder.api.mocker.v0.models.MockApiResponse(
          httpStatusCode = a.res.status,
          body = Option(a.res.body)
        )
      )
    }
    Json.stringify(Json.toJson(apis))
  }
  implicit def makeMockHeaders(mockedApi: MockedApi): Seq[(String, String)] = mockHeaders(Seq(mockedApi))
  implicit def mockHeaders(mockedApis: Seq[MockedApi]): Seq[(String, String)] = {
    Seq(
      sys.env.get(MOCK_API_SECRET)
        .map(secret => (`X-Mock-Api-Secret`, secret))
        .getOrElse((`X-Mock-Api-Secret`, s"FlowAlertError $MOCK_API_SECRET environment setting was not found")),
      (`X-Mock-Apis`, serializeMockedApis(mockedApis))
    )
  }
  implicit def tuppleToTupple[T](t: (MatchRequest, RespondWith[T]) )
    (implicit w: Writes[T], m: Manifest[T]): Seq[(String, String)] = {
    Map(
      t._1 -> RespondWithJson(t._2.status, Json.toJson(t._2.body))
    )
  }


}