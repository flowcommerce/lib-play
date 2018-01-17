package io.flow.play.util

class TextSpec extends LibPlaySpec {

  "truncate" must {

    "does not truncate short text" in {
      Text.truncate("foo") must be("foo")
      Text.truncate("This is") must be("This is")
    }

    "respects limit" in {
      Text.truncate("1234567890", 10) must be("1234567890")
      Text.truncate("12345678900", 10) must be("1234567...")
      Text.truncate("This is a long sentence", 50) must be("This is a long sentence")
      Text.truncate("This is a long sentence", 10) must be("This is...")
    }

    "respects varying suffixes" in {
      Text.truncate("This is a long sentence", 10, None) must be("This is a")
      Text.truncate("This is a long sentence", 10, Some("!")) must be("This is a!")
      Text.truncate("This is a long sentence", 10, Some(" (more)")) must be("Thi (more)")
    }
  }

}
