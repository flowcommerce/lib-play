package io.flow.play.util

import io.flow.play.util.Cache
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.scalatest.concurrent.Eventually.{eventually, timeout}
import org.scalatest.time.{Seconds, Span}

import scala.concurrent.duration._

class CacheSpec extends PlaySpec with OneAppPerSuite {

  private[this] case class TestCache() extends Cache[String, String] {
    private[this] val data = scala.collection.mutable.Map[String, String]()
    var numberRefreshes: Int = 0
    var refreshShouldFail = false

    override val duration: FiniteDuration = FiniteDuration(1, SECONDS)

    override def refresh(key: String): String = {
      if (refreshShouldFail) {
        throw new Exception("test refresh() failing on purpose")
      } else {
        numberRefreshes += 1
        data.getOrElse(key, sys.error("Missing test data for key[$key]"))
      }
    }

    def add(key: String, value: String): Unit = {
      data += (key -> value)
    }
  }

  private[this] def eventuallyInNSeconds[T](n: Int)(f: => T): T = {
    eventually(timeout(Span(n, Seconds))) {
      f
    }
  }

  "cached values are served" in {
    val cache = TestCache()
    cache.add("a", "apple")

    // Test cache hit
    cache.get("a") must equal("apple")
    cache.get("a") must equal("apple")
    cache.numberRefreshes must equal(1)

    // Test cache miss and auto refresh after 1 second
    eventuallyInNSeconds(2) {
      cache.get("a") must equal("apple")
      cache.numberRefreshes must equal(2)
    }
  }

  "supports multiple keys" in {
    val cache = TestCache()
    cache.add("a", "apple")
    cache.add("p", "pear")

    cache.get("a") must equal("apple")
    cache.get("p") must equal("pear")
  }

  "failed refresh handled gracefully" in {
    val cache = TestCache()
    cache.add("a", "apple")
    cache.get("a") must equal("apple")

    cache.refreshShouldFail = true
    cache.add("a", "not apple")

    Thread.sleep(2000)

    // failing refresh should return old value
    cache.get("a") must equal("apple")
  }

}
