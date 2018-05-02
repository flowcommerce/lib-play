package io.flow.play.util

class AuthHeadersSpec extends LibPlaySpec {

  "uses preferred salt" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "a b")
    val salts = Salts(config)

    val authHeaders = new AuthHeaders(config)
    val data = AuthData.Anonymous.Empty

    authHeaders.headers(data).find(_._1 == AuthHeaders.Header).map(_._2).getOrElse {
      sys.error("Failed to find header")
    } must equal(
      data.jwt(salts.preferred)
    )
  }

}
