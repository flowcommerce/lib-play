package io.flow.play.util

object Text {

  private[this] val Ellipsis = "..."

  /** if value is longer than maxLength characters, it wil be truncated to <= (maxLength-Ellipsis.length) characters and
    * an ellipsis added. We try to truncate on a space to avoid breaking a word in pieces.
    *
    * @param value
    *   The string value to truncate
    * @param maxLength
    *   The max length of the returned string, including the final ellipsis if added. Must be >= 10
    * @param ellipsis
    *   If the string is truncated, this value will be appended to the string.
    */
  def truncate(
    value: String,
    maxLength: Int = 80,
    ellipsis: Option[String] = Some(Ellipsis)
  ): String = {
    val suffix = ellipsis.getOrElse("")
    require(maxLength >= suffix.length, s"maxLength must be greater than the length of the suffix[${suffix.length}]")

    if (value.length <= maxLength) {
      value
    } else {
      val pieces = value.split(" ")
      var i = pieces.length
      while (i > 0) {
        val sentence = pieces.slice(0, i).mkString(" ").trim
        val target = sentence + suffix
        if (target.length <= maxLength) {
          return target
        }
        i -= 1
      }

      value.split("").slice(0, maxLength - suffix.length).mkString("") + suffix
    }
  }

}
