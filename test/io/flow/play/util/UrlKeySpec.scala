package io.flow.play.util

import java.util.UUID
import org.scalatest.{FunSpec, Matchers}

class UrlKeySpec extends FunSpec with Matchers {

  val urlKey = UrlKey()

  describe("generate") {

    it("does not generate reserved keys") {
      val key = urlKey.generate("members")()
      urlKey.validate(key) should be(Nil)
    }

    it("handles edge case on length") {
      urlKey.generate("can").length should be(4)
    }
    
    it("executes check function") {
      val sample = UUID.randomUUID.toString
      val key = urlKey.generate(sample) { k => (k != sample) }
      key should be(sample + "-1")
      urlKey.validate(key) should be(Nil)
    }

    it("executes check function multiple times") {
      val sample = UUID.randomUUID.toString
      val key = urlKey.generate(sample) { k => (k != sample) }
      key should be(sample + "-1")
      urlKey.validate(key) should be(Nil)

      val nextKey = urlKey.generate(sample, Some(2)) { k => (k != sample) }
      nextKey should be(sample + "-2")
      urlKey.validate(nextKey) should be(Nil)

    }

    it("appends string to make min length") {
      val urlKey = UrlKey(minKeyLength = 5)

      val key = urlKey.generate("a")
      key.length should be(5)
      key(0).toString should be("a")
      urlKey.validate(key) should be(Nil)
    }

    it("good urls alone") {
      urlKey.generate("foos")() should be("foos")
      urlKey.generate("foos-bar")() should be("foos-bar")
    }

    it("numbers") {
      urlKey.generate("foos123")() should be("foos123")
    }

    it("lower case") {
      urlKey.generate("FOOS-BAR")() should be("foos-bar")
    }

    it("trim") {
      urlKey.generate("  foos-bar  ")() should be("foos-bar")
    }

    it("leading garbage") {
      urlKey.generate("!foos")() should be("foos")
    }

    it("trailing garbage") {
      urlKey.generate("foos!")() should be("foos")
    }

    it("allows underscores") {
      urlKey.generate("ning_1_8_client")() should be("ning_1_8_client")
    }

  }

  describe("validate") {

    it("short") {
      urlKey.validate("bad") should be(Seq(s"Key must be at least ${urlKey.minKeyLength} characters"))
    }

    it("doesn't match generated") {
      urlKey.validate("VALID") should be(Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: valid"))
      urlKey.validate("bad nickname") should be(Seq("Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: bad-nickname"))
    }

    it("reserved") {
      val urlKey = UrlKey(minKeyLength = 1, reservedKeys = Seq("api"))
      urlKey.validate("api") should be(Seq("api is a reserved word and cannot be used for the key"))
    }

    it("label") {
      urlKey.validate("bad", "Id") should be(Seq(s"Id must be at least ${urlKey.minKeyLength} characters"))
      urlKey.validate("no spaces", "Id") should be(Seq("Id must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid id would be: no-spaces"))
    }
  }

}
