package io.flow.play.util


/**
  * Wrapper on the scala random libraries providing higher level
  * common functions.
  */
@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
case class Random() {

  import Random._

  private[this] val random = new java.security.SecureRandom

  /**
    * Generate a random string of length n from the given alphabet
    *
    * @param alphabet The complete set of
    * @param n Length of random string to generate
    */
  def string(alphabet: String)(n: Int): String = {
    assert(n > 0, "n must be > 0")
    val s = new StringBuilder(n)
    var i = n
    while (i > 0) {
      s.append(alphabet(random.nextInt(alphabet.length)))
      i -= 1
    }
    s.toString()
  }

  /**
    * Generate a random string of length n using only a-z (lower case
    * alphabet letters)
    *
    * @param n Length of random string to generate
    */
  def lowercaseAlpha(n: Int): String = {
    string(Lower)(n)
  }

  /**
    * Generate a random string of length n using only a-z and A-Z
    * (alphabet letters only)
    *
    * @param n Length of random string to generate
    */
  def alpha(n: Int): String = {
    string(LowerAndUpper)(n)
  }

  /**
    * Generate a random string of length n using only letters (a-z,
    * A-Z) and numbers (0-9). Also guarantees that the random string
    * will start with a letter, not a number - this is mostly to
    * prevent problems with any applications that infer numeric based
    * on first digit (or strip zeroes).
    *
    * The random string is guaranteed to start with a letter (we do
    * this to avoid confusion in some programs like excel which can
    * infer a numeric type based on the first character)
    *
    * @param n Length of random string to generate
    */
  def alphaNumeric(n: Int): String = {
    if (n == 1) {
      alpha(1)
    } else {
      val s = new StringBuilder(n)
      s.append(alpha(1))
      s.append(string(LowerAndUpperAndNumbers)(n - 1))
      s.toString()
    }
  }

  /**
    * Generate a random string of length n using only letters and
    * numbers that are non ambiguous (e.g. B can look like an 8 so
    * neither B nor 8 is used in the random string). This is a good
    * option for random strings that will be read by humans.
    *
    * The random string is guaranteed to start with a letter (we do
    * this to avoid confusion in some programs like excel which can
    * infer a numeric type based on the first character)
    *
    * @param n Length of random string to generate
    */
  def alphaNumericNonAmbiguous(n: Int): String = {
    if (n == 1) {
      string(NonAmbiguousLowerAndUpper)(1)
    } else {
      val s = new StringBuilder(n)
      s.append(string(NonAmbiguousLowerAndUpper)(1))
      s.append(string(NonAmbiguousLowerAndUpperAndNumbers)(n - 1))
      s.toString()
    }
  }

  /**
    * Generate a random positive int
    */
  final def positiveInt(): Int = random.nextInt() & Int.MaxValue

  /**
    * Generate a random positive long
    */
  final def positiveLong(): Long = random.nextLong() & Long.MaxValue

}

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
object Random {

  private val Ambiguous = "B8G6I1l0OoQDS5Z2".split("")
  private val Numbers = "0123456789"
  private val Lower = "abcdefghijklmnopqrstuvwxyz"
  private val LowerAndUpper = Lower + Lower.toUpperCase
  private val LowerAndUpperAndNumbers = LowerAndUpper + Numbers
  private val NonAmbiguousLowerAndUpper = LowerAndUpper.split("").filter(!Ambiguous.contains(_)).mkString("")
  private val NonAmbiguousLowerAndUpperAndNumbers = NonAmbiguousLowerAndUpper + "3479"

}
