package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class RandomSpec extends FunSpec with Matchers {

  private[this] val random = Random()
  private[this] val Length = 100

  def validate(alphabet: String, values: Seq[String]) {
    val letters = alphabet.split("")
    values.distinct.size should be(values.size)
    values.forall { _.size == Length } should be(true)

    values.foreach { v =>
      v.split("").find { l => !letters.contains(l) } match {
        case None => {}
        case Some(c) => {
          sys.error(s"Found unexpected character[$c] for alphabet[$alphabet] value[$v]")
        }
      }
    }

    val allLetters = values.mkString("").split("").distinct.toSet
    val missing = letters.filter { l => !allLetters.contains(l) }

    // This is a probabilistic check - we choose 3 arbitrarily to
    // minimize chance of a false failure
    if (missing.size > 3) {
      sys.error("Did not find the following expected chars: " + missing.sorted.mkString(", "))
    }
  }

  def validateDoesNotStartWithNumber(values: Seq[String]) {
    val numbers = "0123456789".split("")
    val found = values.filter { v => numbers.contains(v.substring(0, 1)) }
    if (!found.isEmpty) {
      sys.error("Value should not have started with a number: " + values.mkString(", "))
    }
  }

  it("lowercaseAlpha") {
    validate(
      "abcdefghijklmnopqrstuvwxyz",
      1.to(100).map { i => random.lowercaseAlpha(Length) }
    )
  }

  it("alpha") {
    validate(
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
      1.to(100).map { i => random.alpha(Length) }
    )
  }

  it("alphaNumeric") {
    val values = 1.to(100).map { i => random.alphaNumeric(Length) }
    validate(
      s"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
      values
    )
    validateDoesNotStartWithNumber(values)
  }

  it("alphaNumericNonAmbiguous") {
    val values = 1.to(100).map { i => random.alphaNumericNonAmbiguous(Length) }
    validate(
      s"abcdefghijkmnpqrstuvwxyzACEFHJKLMNPRTUVWXY3479",
      values
    )
    validateDoesNotStartWithNumber(values)
  }

  it("positiveInt") {
    val values = 1.to(100).map { i => random.positiveInt() }
    values.distinct.size should be(values.size)
    values.forall { i => i > 0 } should be(true)
  }

  it("positiveLong") {
    val values = 1.to(100).map { i => random.positiveLong() }
    values.distinct.size should be(values.size)
    values.forall { i => i > 0 } should be(true)
  }

  it("string requires positive n") {
    intercept[AssertionError] {
      random.string("a")(0)
    }.getMessage should be("assertion failed: n must be > 0")
  }

}
