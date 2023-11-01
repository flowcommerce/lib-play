package io.flow.play.util

import java.util.UUID

class UrlKeySpec extends LibPlaySpec {

  private[this] val urlKey: UrlKey = UrlKey()

  "generate" must {

    "does not generate reserved keys" in {
      val key = urlKey.generate("members")
      urlKey.validate(key) must be(Nil)
    }

    "handles edge case on length" in {
      urlKey.generate("can").length must be(4)
    }

    "executes check function" in {
      val sample = UUID.randomUUID.toString
      val key = urlKey.generate(sample) { k => (k != sample) }
      key must be(sample + "-1")
      urlKey.validate(key) must be(Nil)
    }

    "executes check function multiple times" in {
      val sample = UUID.randomUUID.toString
      val key = urlKey.generate(sample) { k => (k != sample) }
      key must be(sample + "-1")
      urlKey.validate(key) must be(Nil)

      val nextKey = urlKey.generate(sample, Some(2)) { k => (k != sample) }
      nextKey must be(sample + "-2")
      urlKey.validate(nextKey) must be(Nil)

    }

    "appends string to make min length" in {
      val urlKey = UrlKey(minKeyLength = 5)

      val key = urlKey.generate("a")
      key.length must be(5)
      key(0).toString must be("a")
      urlKey.validate(key) must be(Nil)
    }

    "good urls alone" in {
      urlKey.generate("foos") must be("foos")
      urlKey.generate("foos-bar") must be("foos-bar")
    }

    "numbers" in {
      urlKey.generate("foos123") must be("foos123")
    }

    "lower case" in {
      urlKey.generate("FOOS-BAR") must be("foos-bar")
    }

    "trim" in {
      urlKey.generate("  foos-bar  ") must be("foos-bar")
    }

    "leading garbage" in {
      urlKey.generate("!foos") must be("foos")
    }

    "trailing garbage" in {
      urlKey.generate("foos!") must be("foos")
    }

    "preserves leading dash" in {
      urlKey.generate("-foos") must be("-foos")
      urlKey.generate("--foos") must be("--foos")
    }

    "preserves trailing dash" in {
      urlKey.generate("foos-") must be("foos-")
      urlKey.generate("foos--") must be("foos--")
    }

    "allows underscores" in {
      urlKey.generate("ning_1_8_client") must be("ning_1_8_client")
    }

  }

  "validate" must {

    "short" in {
      urlKey.validate("bad") must be(Seq(s"Key must be at least ${urlKey.minKeyLength} characters"))
    }

    "doesn't match generated" in {
      urlKey.validate("VALID") must be(
        Seq(
          "Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: valid"
        )
      )
      urlKey.validate("bad nickname") must be(
        Seq(
          "Key must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid key would be: badnickname"
        )
      )
    }

    "reserved" in {
      val urlKey = UrlKey(minKeyLength = 1, reservedKeys = Seq("api"))
      urlKey.validate("api") must be(Seq("api is a reserved word and cannot be used for the key"))
    }

    "label" in {
      urlKey.validate("bad", "Id") must be(Seq(s"Id must be at least ${urlKey.minKeyLength} characters"))
      urlKey.validate("no spaces", "Id") must be(
        Seq(
          "Id must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid id would be: nospaces"
        )
      )
    }

    "trailing characters" in {
      urlKey.validate("trailing-") must be(Nil)
    }
  }

}
