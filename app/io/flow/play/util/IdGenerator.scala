package io.flow.play.util

import java.util.UUID
import org.joda.time.{DateTime, DateTimeZone}

object IdGenerator {

  // Some from http://www.freewebheaders.com/full-list-of-bad-words-banned-by-google/
  val PrefixBlackList = Seq("ass", "a2m", "a55", "bch", "bum", "but", "cok", "cox", "cum", "die", "dik", "fag", "fck", "fuk", "fux", "god", "jap", "kkk", "kok", "kum", "lik", "lsd", "nob", "omg", "psy", "pus", "sex", "sht", "suk", "tit", "xxx")

  val PrefixLength = 3
  val Separator = "-"
}

/**
  * Generates a new unique ID for a resource. Main feature is to
  * prefix the unique id with a three character identifer to
  * understand how the ID is used (e.g. 'usr')
  * 
  * @param prefix Global prefix to identify the type of resource for which you
  *         are generating an ID. Must be 3 characters, lowercase.
  */
case class IdGenerator(
  prefix: String
) {
  assert(prefix.toLowerCase == prefix, s"prefix[$prefix] must be in lower case")
  assert(prefix.trim == prefix, s"prefix[$prefix] must be trimmed")
  assert(prefix.length == IdGenerator.PrefixLength, s"prefix[$prefix] must be ${IdGenerator.PrefixLength} characters long")
  assert(!IdGenerator.PrefixBlackList.contains(prefix), s"prefix[$prefix] is on the black list and cannot be used")

  private[this] val idFormat = Seq("%s", "%s").mkString(IdGenerator.Separator)

  def randomId(): String = {
    idFormat.format(prefix, UUID.randomUUID.toString.replaceAll("\\-", ""))
  }

}
