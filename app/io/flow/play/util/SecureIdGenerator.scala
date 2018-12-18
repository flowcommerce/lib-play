package io.flow.play.util

object SecureIdGenerator {
  val MinPrefixLength = 3
  // M and W are both 3 characters in the pager code alphabet.
  val MaxPrefixLength = 6
}

/**
  * Generates a new, cryptographically secure, unique ID for a resource.
  * Main feature is to prefix the unique id with a three character identifer to
  * understand how the ID is used (e.g. 'F61').
  *
  * Secure id prefixes use the following format:
  *   https://github.com/flowcommerce/standards/blob/master/Naming.md
  * If adding a new prefix, create a pull request to update the `Flow Prefixes`
  * section at the bottom of the document so others are aware.
  *
  * @param prefix Global prefix to identify the type of resource for which you
  *         are generating an ID. Must be 3-6 characters, uppercase.
  */
case class SecureIdGenerator(
  prefix: String
) {
  assert(prefix.trim == prefix, s"prefix[$prefix] must be trimmed")
  assert(prefix.toUpperCase == prefix, s"prefix[$prefix] must be in upper case")
  assert(prefix.startsWith("F"), s"prefix[$prefix] must begin with the letter 'F'")
  assert(
    prefix.length >= SecureIdGenerator.MinPrefixLength &&
    prefix.length <= SecureIdGenerator.MaxPrefixLength,
    s"prefix[$prefix] must be between " +
    s"${SecureIdGenerator.MinPrefixLength} and ${SecureIdGenerator.MaxPrefixLength} characters"
  )
  assert(!io.flow.util.BadWords.contains(prefix), s"prefix[$prefix] is on the black list and cannot be used")


  private[this] val tokenLength = 64 - prefix.length
  private[this] val random = io.flow.util.Random()

  def randomId(): String = {
    prefix + random.alphaNumeric(tokenLength)
  }

}
