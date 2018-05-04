package io.flow.play.util

import scala.util.{Failure, Success, Try}

class SaltsSpec extends LibPlaySpec {

  private[this] def verifyException(message: String)(f: => Any) = {
    Try {
      f
    } match {
      case Success(_) => sys.error("Expected error")
      case Failure(ex) => ex.getMessage must equal(message)
    }
  }

  "all raises appropriate error if salt not set" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "   ")
    verifyException("FlowError Configuration variable[JWT_SALTS] is required") {
      Salts(config).all
    }
  }

  "all uses JWT_SALT if only option" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "")
    config.set("JWT_SALT", "test")
    Salts(config).all must equal(Seq("test"))
  }

  "all prefers JWT_SALTS" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "a b")
    config.set("JWT_SALT", "test")
    Salts(config).all must equal(Seq("a", "b"))
  }

  "preferred is last salt" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "a b")
    Salts(config).preferred must equal("b")
  }

  "isJsonWebTokenValid" in {
    val config = createMockConfig()
    config.set("JWT_SALTS", "a b")
    val salts = Salts(config)

    salts.isJsonWebTokenValid(AuthData.Anonymous.empty().jwt("a")) must be(true)
    salts.isJsonWebTokenValid(AuthData.Anonymous.empty().jwt("b")) must be(true)
    salts.isJsonWebTokenValid(AuthData.Anonymous.empty().jwt("c")) must be(false)
  }

}
