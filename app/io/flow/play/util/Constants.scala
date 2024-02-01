package io.flow.play.util

object Constants {
  object Headers {
    val UserAgent = "User-Agent"
    val Authorization = "Authorization"

    val FlowAuth = "X-Flow-Auth"
    val FlowRequestId = "X-Flow-Request-Id"
    val FlowServer = "X-Flow-Server"
    val FlowHost = "X-Flow-Host"
    val FlowIp = "X-Flow-Ip"
    val FlowProxyResponseTime = "X-Flow-Proxy-Response-Time"
    val FlowProxyServiceTiming = "X-Flow-Proxy-Service-Timing"
    val ApiDocVersion = "X-Apidoc-Version"
    val RequestTime = "Request-Time"

    val ContentType = "Content-Type"
    val ContentLength = "Content-Length"
    val Host = "Host"
    val ForwardedHost = "X-Forwarded-Host"
    val ForwardedFor = "X-Forwarded-For"
    val Origin = "Origin"
    val ForwardedOrigin = "X-Forwarded-Origin"
    val ForwardedMethod = "X-Forwarded-Method"

    val CfRay = "CF-RAY"
    val CfConnectingIp = "CF-Connecting-IP"
    val CfTrueClientIp = "True-Client-IP"
    val CfIpCountry = "CF-IPCountry"
    val CfVisitor = "CF-Visitor"
    val DatadogTraceId = "X-Datadog-Trace-Id"
  }
}
