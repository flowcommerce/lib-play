package io.flow.play.util

import akka.stream.Materializer
import io.flow.log.RollbarLogger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.HttpFilters

/** To use in any Flow app depending on lib-play:
  *
  * (1) Add this to your base.conf: play.http.filters=io.flow.play.util.LoggingFilter
  */

class LoggingFilter @javax.inject.Inject() (loggingFilter: FlowLoggingFilter) extends HttpFilters {
  def filters: Seq[Filter] = Seq(loggingFilter)
}

class FlowLoggingFilter @javax.inject.Inject() (implicit
  ec: ExecutionContext,
  m: Materializer,
  logger: RollbarLogger,
  config: Config,
) extends Filter {

  private val LoggedRequestMethodConfig = "play.http.logging.methods"

  private val LoggedHeaders = Seq(
    Constants.Headers.UserAgent,
    Constants.Headers.ForwardedFor,
    Constants.Headers.CfConnectingIp,
    Constants.Headers.CfTrueClientIp,
    Constants.Headers.CfRay,
    Constants.Headers.ApiDocVersion,
    Constants.Headers.FlowIp,
    Constants.Headers.FlowRequestId,
    Constants.Headers.DatadogTraceId,
  )

  private val loggedRequestMethods =
    config.optionalList(LoggedRequestMethodConfig).map(_.toSet)

  def apply(f: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis
    f(requestHeader).map { result =>
      /*
       * If user defined a list of methods that produce logs, then use that
       * Otherwise default to everything
       */
      if (loggedRequestMethods.fold(true)(_.contains(requestHeader.method))) {
        val requestTime = System.currentTimeMillis - startTime
        val headerMap = requestHeader.headers.toMap

        val userAgent = headerMap.getOrElse(Constants.Headers.UserAgent, Nil).mkString("[", ",", "]")
        val flowIp = headerMap.getOrElse(Constants.Headers.FlowIp, Nil).mkString(",")
        val flowRequestId = headerMap.getOrElse(Constants.Headers.FlowRequestId, Nil).mkString(",")

        val line = Seq(
          requestHeader.method,
          s"${requestHeader.host}${requestHeader.uri}",
          requestHeader.version,
          result.header.status.toString,
          s"${requestTime}ms",
          userAgent,
        ).mkString(" ")

        val loggedRequestHeaders = (for {
          header <- LoggedHeaders
          value <- headerMap.get(header)
        } yield header -> value).toMap

        logger
          .withKeyValue("https", requestHeader.secure)
          .withKeyValue("http_version", requestHeader.version)
          .withKeyValue("method", requestHeader.method)
          .withKeyValue("host", requestHeader.host)
          .withKeyValue("path", requestHeader.path)
          .withKeyValue("query_params", requestHeader.queryString)
          .withKeyValue("http_code", result.header.status)
          .withKeyValue("request_time_ms", requestTime)
          .withKeyValue("request_headers", loggedRequestHeaders)
          .withKeyValue("x-flow-ip", flowIp)
          .withKeyValue("x-flow-request-id", flowRequestId)
          .info(line)
      }

      result
    }
  }

  override implicit def mat: Materializer = m
}
