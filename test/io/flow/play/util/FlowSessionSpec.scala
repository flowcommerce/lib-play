package io.flow.play.util

import cats.data.Validated.{Invalid, Valid}
import io.flow.util.Constants

class FlowSessionSpec extends LibPlaySpec {

  "validate" must {

    "accept valid session id" in {
      FlowSession.validate(s"${Constants.Prefixes.Session}${createTestId()}") match {
        case Valid(_) => // no-op
        case Invalid(e) => sys.error(s"Expected valid session: ${e}")
      }
    }

    "reject invalid session id" in {
      FlowSession.validate(s"foo${createTestId()}") match {
        case Valid(_) => sys.error(s"Expected invalid session")
        case Invalid(e) => e mustBe "Flow session id must start with 'F51' and not[foo]"
      }
    }

  }
}
