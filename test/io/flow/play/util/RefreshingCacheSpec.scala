package io.flow.play.util

import org.mockito.Mockito
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers, OptionValues}
import org.scalatestplus.play.OneAppPerSuite

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration._

class RefreshingCacheSpec extends FlatSpec with OneAppPerSuite with Matchers with OptionValues with MockitoSugar
  with Eventually {

  def createCache[K, V](reloadPeriod: FiniteDuration, retrieve: () => Map[K, V], maxAttempts: Int = 1): RefreshingCache[Map[K, V]] =
    RefreshingCache(app.actorSystem.scheduler, Implicits.global, reloadPeriod, retrieve, maxAttempts)

  "RefreshingCache" should "load and get" in {
    val cache = createCache[String, Int](1.minute, () => Map("1" -> 1))

    cache.get shouldBe Map("1" -> 1)
  }

  it should "fail to initialize if retrieve fails at startup" in {
    val retrieve = () => throw new IllegalStateException("boom")
    an[IllegalStateException] should be thrownBy createCache(1.minute, retrieve)
  }

  it should "refresh data" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("1" -> 1))
      .thenReturn(Map("2" -> 2))

    val cache = createCache(50.millis, retrieve)

    cache.get shouldBe Map("1" -> 1)
    eventually(Timeout(100.millis)) {
      cache.get shouldBe Map("2" -> 2)

    }
  }

  it should "return old data if retrieve fails" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("2" -> 2))
      .thenThrow(new IllegalStateException("boom"))

    val cache = createCache(10.millis, retrieve)

    cache.get shouldBe Map("2" -> 2)
    Thread.sleep(50)
    cache.get shouldBe Map("2" -> 2)
  }

  it should "retry to retrieve" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("1" -> 1, "2" -> 2))
      .thenThrow(new IllegalStateException("boom"))
      .thenReturn(Map("3" -> 3, "4" -> 4))

    val cache = createCache(50.millis, retrieve, maxAttempts = 3)

    cache.get shouldBe Map("1" -> 1, "2" -> 2)
    eventually(Timeout(80.millis)) {
      cache.get shouldBe Map("3" -> 3, "4" -> 4)
    }
  }

}
