package io.flow.play.util

import org.scalatest.{FunSpec, Matchers}

class IdGeneratorSpec extends FunSpec with Matchers {

  private[this] val SourcePath = "app/io/flow/play/util/IdGenerator.scala"
  private[this] val RequiredPrefixLength = 3
  private[this] val MinimumRandomLength = 16
  private[this] val MinimumGeneratorsExpected = 5
  private[this] val IdGeneratorRegexp = """^case object (\w+) extends IdGenerator \{ override val prefix = "(\w+)" \}$""".r

  private[this] case class ParsedIdGenerator(name: String, prefix: String)

  private[this] val generators = Seq(
    IdGenerator.Carrier, IdGenerator.Center, IdGenerator.Region, IdGenerator.Ruleset, IdGenerator.ServiceLevel
  )

  // Parse the source file to extract declared generators
  private[this] def parseGenerators(failOnError: Boolean = false): Seq[ParsedIdGenerator] = {
    scala.io.Source.fromFile(new java.io.File(SourcePath)).getLines.
      filter { l => l.indexOf(" extends IdGenerator") > 0 }.
      map(_.trim).
      flatMap { l =>
        l match {
          case IdGeneratorRegexp(name, prefix) => {
            Some(ParsedIdGenerator(name, prefix))
          }
          case _ => {
            if (failOnError) {
              sys.error(s"$SourcePath: IdGenerator object did not match expected format: $l")
            }
            None
          }
        }
      }.toSeq
  }

  it("parsed generators non empty") {
    if (parseGenerators().size < MinimumGeneratorsExpected) {
      fail(s"$SourcePath: Expected to find at least $MinimumGeneratorsExpected prefixes but only found[${parseGenerators().size}]")
    }
  }

  it("generators declared in standard format") {
    parseGenerators(failOnError = true)
  }

  it("generators declares all") {
    val missing = parseGenerators().find { g =>
      !generators.map(_.prefix).contains(g.prefix)
    }
    if (!missing.isEmpty) {
      fail("Test case does not document all IdGenerators. Missing: " + missing.map(_.name).mkString(", "))
    }
  }

  it("randomId") {
    val generator = IdGenerator.Carrier
    val ids = 1.to(100).map { _ => generator.randomId() }
    ids.size should be(100)
    ids.distinct.size should be(ids.size)
  }

  it("randomId respects prefix") {
    IdGenerator.Carrier.randomId().startsWith(IdGenerator.Carrier.prefix + IdGenerator.Separator) should be(true)
    IdGenerator.Center.randomId().startsWith(IdGenerator.Center.prefix + IdGenerator.Separator) should be(true)
  }

  it("length is accurate") {
    IdGenerator.Carrier.randomId().length should be(IdGenerator.Carrier.length())
  }

  it("randomLength exceeds minimum") {
    generators.foreach { generator =>
      if (generator.randomLength < MinimumRandomLength) {
        fail(s"IdGenerator[${generator}] randomLength[${generator.randomLength}] is too short - minimum is $MinimumRandomLength")
      }
    }
  }

  it("generator names and values meet requirements") {
    parseGenerators().foreach { gen =>
      if (gen.prefix.toLowerCase != gen.prefix) {
        fail(s"$SourcePath: IdGenerator[${gen.name}] prefix[${gen.prefix}] must be in lower case")
      } else if (gen.prefix.trim != gen.prefix) {
        fail(s"$SourcePath: IdGenerator[${gen.name}] prefix[${gen.prefix}] must be trimmed")
      } else if (gen.prefix.length != RequiredPrefixLength) {
        fail(s"$SourcePath: IdGenerator[${gen.name}] prefix[${gen.prefix}] must be exactly $RequiredPrefixLength characters")
      } else if (gen.name.substring(0, 1) != gen.name.substring(0, 1).toUpperCase) {
        fail(s"$SourcePath: IdGenerator[${gen.name}] must start with a capital letter")
      }
    }
  }

  it("All prefixes are unique and standard") {
    parseGenerators().groupBy(_.prefix).foreach { case (prefix, prefixes) =>
      if (prefixes.length > 1) {
        fail(s"$SourcePath: IdGenerator prefix[$prefix] is repeated: " + prefixes.map(_.name).mkString(", "))
      }
    }

    val actual = parseGenerators().map(_.name)
    val sorted = parseGenerators().map(_.name).sorted
    if (actual != sorted) {
      fail(s"$SourcePath: IdGenerators must be sorted alphabetically. Expected order to be[${sorted.mkString(", ")}] but found[${actual.mkString(", ")}]")
    }
  }

}
