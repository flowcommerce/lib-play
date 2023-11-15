package io.flow.play.util

import java.util.UUID

object SecureIdGenerator {
  val MinPrefixLength = 3
  // M and W are both 3 characters in the pager code alphabet.
  val MaxPrefixLength = 6
}

/** Generates a new, unqiue, cryptographically secure, unique ID for a resource. Main feature is to prefix the unique id
  * with a three character identifier to understand how the ID is used (e.g. 'F61').
  *
  * Secure id prefixes use the following format:
  * https://www.notion.so/flow/Naming-b72c34dc49484a98a3b7b8fcbea65d1e#e4e4445685a24538a87a880d7149366c If adding a new
  * prefix, create a pull request to update the `Flow Prefixes` section at the bottom of the document so others are
  * aware.
  *
  * @param prefix
  *   Global prefix to identify the type of resource for which you are generating an ID. Must be 3-6 characters,
  *   uppercase.
  */
case class SecureIdGenerator(
  prefix: String,
) {
  assert(prefix.trim == prefix, s"prefix[$prefix] must be trimmed")
  assert(prefix.toUpperCase == prefix, s"prefix[$prefix] must be in upper case")
  assert(prefix.startsWith("F"), s"prefix[$prefix] must begin with the letter 'F'")
  assert(
    prefix.length >= SecureIdGenerator.MinPrefixLength &&
      prefix.length <= SecureIdGenerator.MaxPrefixLength,
    s"prefix[$prefix] must be between " +
      s"${SecureIdGenerator.MinPrefixLength} and ${SecureIdGenerator.MaxPrefixLength} characters",
  )
  assert(!io.flow.util.BadWords.contains(prefix), s"prefix[$prefix] is on the black list and cannot be used")

  private[this] def generateAndFormatUUID(): String = {
    UUID.randomUUID().toString.replaceAll("-", "")
  }

  private[this] val tokenLength = 64 - prefix.length - generateAndFormatUUID().length
  private[this] val random = io.flow.util.Random()

  def randomId(): String = {
    prefix + random.alphaNumeric(tokenLength) + generateAndFormatUUID()
  }

}
