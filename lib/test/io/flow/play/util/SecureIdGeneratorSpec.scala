package io.flow.play.util

class SecureIdGeneratorSpec extends LibPlaySpec {

  "prefix must be between 3 - 6 characters" in {
    intercept[AssertionError] {
      SecureIdGenerator("FO")
    }.getMessage must be("assertion failed: prefix[FO] must be between 3 and 6 characters")

    intercept[AssertionError] {
      SecureIdGenerator("FOFOFOFO")
    }.getMessage must be("assertion failed: prefix[FOFOFOFO] must be between 3 and 6 characters")
  }

  "prefix must be upper case" in {
    intercept[AssertionError] {
      SecureIdGenerator("foo")
    }.getMessage must be("assertion failed: prefix[foo] must be in upper case")
  }

  "prefix must be trimmed" in {
    intercept[AssertionError] {
      SecureIdGenerator("  FOO  ")
    }.getMessage must be("assertion failed: prefix[  FOO  ] must be trimmed")
  }

  "prefix is not on black list" in {
    intercept[AssertionError] {
      SecureIdGenerator("FECK")
    }.getMessage must be("assertion failed: prefix[FECK] is on the black list and cannot be used")
  }

  "randomId must start with prefix" in {
    val generator = SecureIdGenerator("F12")
    val id = generator.randomId()
    id.startsWith("F12") must be(true)
  }

  "randomId must be 64 characters long" in {
    val generator = SecureIdGenerator("F12")
    val num = 10000
    val ids = 1.to(num).map { _ => generator.randomId() }
    ids.size must be(num)
    ids.distinct.size must be(ids.size)
    ids.foreach { _.length must equal(64) }
  }
}
