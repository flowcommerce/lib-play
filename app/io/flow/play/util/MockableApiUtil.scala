package io.flow.play.util

import akka.util.ByteString
import io.flow.apibuilder.api.mocker.v0.models.{MockApi, MockApiResponse}
import play.api.http.HttpEntity
import play.api.libs.json.{Json, Reads}
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

  val `X-Mock-Api-Secret` = "X-Mock-Api-Secret"
  val `X-Mock-Apis` = "X-Mock-Apis"
  val MOCK_API_SECRET = "MOCK_API_SECRET"

  def withMockableApis(method: String, url: String, requestHeaders: Seq[(String, String)])
    (f: => Future[WSResponse])
    (implicit ec: ExecutionContext): Future[WSResponse] = {
    (for {
      secretFromHeader <- fromHeaders(requestHeaders, `X-Mock-Api-Secret`)
      secret <- fromEnvironmentVariable(MOCK_API_SECRET)
      mockApis <- fromJsonHeaders[Seq[MockApi]](requestHeaders, `X-Mock-Apis`)
      mockApi <- findMockApi(mockApis, method, url)
      mockApiResponse <- getMockApiResponse(mockApi, secretFromHeader, secret)
    } yield mockApiResponse)
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
  }

  def withMockableApis[T](request: Request[T])
    (f: => Future[Result])
    (implicit ec: ExecutionContext): Future[Result] = {
    val headers = request.headers.toMap
    val method = request.method.toUpperCase
    val url = request.uri
    (for {
      secretFromHeader <- fromHeaders(headers, `X-Mock-Api-Secret`)
      secret <- fromEnvironmentVariable(MOCK_API_SECRET)
      mockApis <- fromJsonHeaders[Seq[MockApi]](headers, `X-Mock-Apis`)
      mockApi <- findMockApi(mockApis, method, url)
      mockApiResponse <- getMockApiResponse(mockApi, secretFromHeader, secret)
    } yield mockApiResponse)
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

  def fromEnvironmentVariable(name: String): Option[String] = sys.env.get(name)

  def findMockApi(mockApis: Seq[MockApi], method: String, url: String): Option[MockApi] =
    mockApis.find(mockApi => mockApi.request.url.toUpperCase == url.toUpperCase && mockApi.request.method.toUpperCase == method.toUpperCase)

  def getMockApiResponse(mockApi: MockApi, secretFromHeader: String, secretFromEnvironmentVariable: String): Option[MockApiResponse] =
    if (secretFromHeader == secretFromEnvironmentVariable) Option(mockApi.response) else None

  implicit def headersToSeq(h: Map[String, Seq[String]]): Seq[(String, String)] =
    h.foldLeft(Seq.empty[(String, String)]) {
      case (acc, (key, values)) => acc ++ values.map(value => (key, value))
    }

}