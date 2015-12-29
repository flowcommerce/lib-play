package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class IdGeneratorSpec extends FunSpec with Matchers {

  private[this] val MinimumRandomLength = 16

  it("prefix must be 3 characters") {
    intercept[AssertionError] {
      IdGenerator("fo")
    }.getMessage should be("assertion failed: prefix[fo] must be 3 characters long")
  }

  it("prefix must be lower case") {
    intercept[AssertionError] {
      IdGenerator("FOO")
    }.getMessage should be("assertion failed: prefix[FOO] must be in lower case")
  }

  it("prefix must be trimmed") {
    intercept[AssertionError] {
      IdGenerator("  foo  ")
    }.getMessage should be("assertion failed: prefix[  foo  ] must be trimmed")
  }

  it("randomId must start with prefix") {
    val generator = IdGenerator("tst")
    val id = generator.randomId()
    id.startsWith("tst-") should be(true)
  }

  it("randomId") {
    val generator = IdGenerator("tst")
    val ids = 1.to(100).map { _ => generator.randomId() }
    ids.size should be(100)
    ids.distinct.size should be(ids.size)
    ids.foreach { _.length should be >=(MinimumRandomLength) }
  }

}
