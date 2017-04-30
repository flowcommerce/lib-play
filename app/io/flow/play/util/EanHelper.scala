package io.flow.play.util

/**
  * Generates and validates EAN string
  *
  */
object EanHelper {

  val codeLength = 12
  val fullLength = 13
  private[this] val random = new Random()

  def generate(): String = {
    val s = random.numeric(codeLength)
    Seq("%s", "%s").mkString("").format(s, checksum(s))
  }

  def validate(s: String): Seq[String] = {
    val formatErrors = s.forall(_.isDigit) match {
      case false => Seq("Input should be numeric string")
      case true => Nil
    }

    val lengthErrors = s.size == fullLength match {
      case false => Seq(s"Input should be $fullLength chars")
      case true => Nil
    }

    lengthErrors ++ formatErrors match {
      case Nil => {
        val checkDigit = s.last.toString.toInt
        val input = s.slice(0, s.size - 1)
        checksum(input) == checkDigit match {
          case false => Seq("Check digit is incorrect")
          case true => Nil
        }
      }
      case errors => {
        errors
      }
    }
  }

  /**
    * Calculate checksum
    * See: http://forums.devx.com/showthread.php?172712-how-to-calculate-the-check-digit-(EAN-13)-barcode-symbologies
    *
    * @param s Input numeric string whose checksum we need to calculate
    */
  def checksum(s: String): Int = {
    assert(s.forall(_.isDigit), s"Input [$s] should be numeric string")
    assert(s.size == codeLength, s"Input [$s] should be $codeLength chars")

    var x: Int = 0
    var y: Int = 0

    s.reverse.split("").zipWithIndex.foreach { case (curr, i) =>
      if (i % 2 == 0)
        x += curr.toInt
      else
        y += curr.toInt
    }

    val sum = (x * 3) + y
    val nextMultipleOfTen = (math.ceil(sum / 10.00) * 10).toInt

    nextMultipleOfTen - sum
  }
}

