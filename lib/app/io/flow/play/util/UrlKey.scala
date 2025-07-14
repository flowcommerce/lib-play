package io.flow.play.util

/** Class designed to help generate URL friendly keys, with methods to create unique tokens.
  *
  * @param minKeyLength
  *   Minimum length of key used for validation
  * @param reservedKeys
  *   Optionally validate that key cannot be one of these
  * @param maxIterations
  *   Upper limit on the number of iterations before we raise an error. Used primarily in practice to prevent infinite
  *   loops in key generation.
  */

case class UrlKey(
  minKeyLength: Int = 4,
  reservedKeys: Seq[String] = Nil,
  maxIterations: Int = 1000,
) {
  assert(minKeyLength > 0, s"minKeyLength[$minKeyLength] must be > 0")
  assert(maxIterations > 0, s"maxIterations[$maxIterations] must be > 0")

  private[this] val random = io.flow.util.Random()

  private[this] val AcceptedChars: Set[String] = "0123456789abcdefghijklmnopqrstuvwxyz.-_".split("").toSet

  /** Deterministically generated a unique key that is URL safe.
    *
    * @param checkFunction
    *   Optionally provide your own function that accepts the generated key and returns true / false. If false, we will
    *   iterate to create another key that you can check. This lets you do things like check uniqueness of the key
    *   against an external source (e.g. database table)
    */
  @scala.annotation.tailrec
  final def generate(
    value: String,
    suffix: Option[Int] = None,
  )(implicit
    checkFunction: String => Boolean = { _ => true },
  ): String = {
    val formatted = format(value)

    (formatted.length < minKeyLength) match {
      case true => {
        generate(formatted + random.alphaNumeric(minKeyLength - formatted.length), suffix)(checkFunction)
      }

      case false => {
        val (key, nextSuffix) = suffix match {
          case None => (formatted, 1)
          case Some(i) => (formatted + s"-$i", i + 1)
        }

        validate(key) match {
          case Nil => {
            (checkFunction(key)) match {
              case true => key
              case false => generate(value, Some(nextSuffix))(checkFunction)
            }
          }
          case _ => {
            if (nextSuffix > maxIterations) {
              sys.error(s"Possible infinite loop generating key for value[$value]")
            }
            generate(value, Some(nextSuffix))(checkFunction)
          }
        }
      }
    }
  }

  /** Takes a string and formats it to be url safe, removing non safe url characters and replacing while trying to
    * maximize legibility.
    */
  def format(value: String): String = {
    value.toLowerCase.trim.split("").filter(AcceptedChars.contains).mkString("")
  }

  def validate(
    key: String,
    label: String = "Key",
    lowerLabel: Option[String] = None,
  ): Seq[String] = {
    val generated = format(key)
    if (key.length < minKeyLength) {
      Seq(s"$label must be at least $minKeyLength characters")
    } else if (key != generated) {
      Seq(
        s"$label must be in all lower case and contain alphanumerics only (-, _, and . are supported). A valid ${label.toLowerCase} would be: $generated",
      )
    } else {
      val lower = lowerLabel.getOrElse(label.toLowerCase)
      reservedKeys.find(_ == generated) match {
        case Some(_) => Seq(s"$key is a reserved word and cannot be used for the $lower")
        case None => Nil
      }
    }
  }

}
