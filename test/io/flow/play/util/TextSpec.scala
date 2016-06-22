package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class TextSpec extends FunSpec with Matchers {

  describe("truncate") {

    it("does not truncate short text") {
      Text.truncate("foo") should be("foo")
      Text.truncate("This is") should be("This is")
    }

    it("respects limit") {
      Text.truncate("1234567890", 10) should be("1234567890")
      Text.truncate("12345678900", 10) should be("1234567...")
      Text.truncate("This is a long sentence", 50) should be("This is a long sentence")
      Text.truncate("This is a long sentence", 10) should be("This is...")
    }

    it("respects varying suffixes") {
      Text.truncate("This is a long sentence", 10, None) should be("This is a")
      Text.truncate("This is a long sentence", 10, Some("!")) should be("This is a!")
      Text.truncate("This is a long sentence", 10, Some(" (more)")) should be("Thi (more)")
    }
  }

}
