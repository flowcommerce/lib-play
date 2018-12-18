package io.flow.play.util

import com.github.ghik.silencer.silent

@silent class RandomSpec extends LibPlaySpec {

  private[this] val random = Random()
  private[this] val Length = 100

  def validate(alphabet: String, values: Seq[String]): Unit = {
    val letters = alphabet.split("")
    values.distinct.size must be(values.size)
    values.forall { _.size == Length } must be(true)

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

  def validateDoesNotStartWithNumber(values: Seq[String]): Unit = {
    val numbers = "0123456789".split("")
    val found = values.filter { v => numbers.contains(v.substring(0, 1)) }
    if (!found.isEmpty) {
      sys.error("Value must not have started with a number: " + values.mkString(", "))
    }
  }

  "lowercaseAlpha" in {
    validate(
      "abcdefghijklmnopqrstuvwxyz",
      1.to(100).map { _ => random.lowercaseAlpha(Length) }
    )
  }

  "alpha" in {
    validate(
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
      1.to(100).map { _ => random.alpha(Length) }
    )
  }

  "alphaNumeric" in {
    val values = 1.to(100).map { _ => random.alphaNumeric(Length) }
    validate(
      s"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
      values
    )
    validateDoesNotStartWithNumber(values)
  }

  "alphaNumericNonAmbiguous" in {
    val values = 1.to(100).map { _ => random.alphaNumericNonAmbiguous(Length) }
    validate(
      s"abcdefghijkmnpqrstuvwxyzACEFHJKLMNPRTUVWXY3479",
      values
    )
    validateDoesNotStartWithNumber(values)
  }

  "positiveInt" in {
    val values = 1.to(100).map { _ => random.positiveInt() }
    values.distinct.size must be(values.size)
    values.forall { i => i > 0 } must be(true)
  }

  "positiveLong" in {
    val values = 1.to(100).map { _ => random.positiveLong() }
    values.distinct.size must be(values.size)
    values.forall { i => i > 0 } must be(true)
  }

  "string requires positive n" in {
    intercept[AssertionError] {
      random.string("a")(0)
    }.getMessage must be("assertion failed: n must be > 0")
  }

}
