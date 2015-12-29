package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class IdGeneratorSpec extends FunSpec with Matchers {

  private[this] val SourcePath = "app/io/flow/play/util/IdGenerator.scala"
  private[this] val RequiredPrefixLength = 3
  private[this] val MinimumPrefixesExpected = 5
  private[this] val PrefixRegexp = """^case object (\w+) extends Prefix \{ override def toString\(\) = "(\w+)" \}$""".r

  private[this] case class PrefixValue(name: String, value: String)

  it("randomId") {
    val generator = IdGenerator(Prefix.Carrier)
    val ids = 1.to(100).map { _ => generator.randomId() }
    ids.size should be(100)
    ids.distinct.size should be(ids.size)
  }

  it("randomId respects prefix") {
    IdGenerator(Prefix.Carrier).randomId().startsWith(Prefix.Carrier.toString() + IdGenerator.Separator) should be(true)
    IdGenerator(Prefix.Center).randomId().startsWith(Prefix.Center.toString() + IdGenerator.Separator) should be(true)
  }

  it("All prefixes are unique and standard") {
    val all = scala.io.Source.fromFile(new java.io.File(SourcePath)).getLines.
      filter { l => l.indexOf(" extends Prefix") > 0 }.
      map(_.trim).
      map { l =>
        l match {
          case PrefixRegexp(name, value) => {
            if (value.toLowerCase != value) {
              fail(s"$SourcePath: Prefix[$name] value[$value] must be in lower case")
          } else if (value.trim != value) {
              fail(s"$SourcePath: Prefix[$name] value[$value] must be trimmed")
            } else if (value.length != RequiredPrefixLength) {
              fail(s"$SourcePath: Prefix[$name] value[$value] must be exactly $RequiredPrefixLength characters")
            } else if (name.substring(0, 1) != name.substring(0, 1).toUpperCase) {
              fail(s"$SourcePath: Prefix[$name] must start with a capital letter")
            }
            PrefixValue(name, value)
          }
          case _ => {
            fail(s"$SourcePath: Prefix object did not match expected format: $l")
          }
        }
      }.toList

    if (all.size < MinimumPrefixesExpected) {
      fail(s"$SourcePath: Expected to find at least $MinimumPrefixesExpected prefixes but only found[${all.size}]")
    }

    all.groupBy(_.value).foreach { case (value, prefixes) =>
      if (prefixes.length > 1) {
        fail(s"$SourcePath: Prefix[$value] is repeated: " + prefixes.map(_.name).mkString(", "))
      }
    }

    val actual = all.map(_.name)
    val sorted = all.map(_.name).sorted
    if (actual != sorted) {
      fail(s"$SourcePath: Prefixes must be sorted alphabetically. Expected order to be[${sorted.mkString(", ")}] but found[${actual.mkString(", ")}]")
    }
  }

}
