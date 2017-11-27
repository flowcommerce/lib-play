package io.flow.play.util

import akka.actor.Scheduler
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

  def createCache[K, V](reloadPeriod: FiniteDuration, retrieveAll: () => Map[K, V], maxAttempts: Int = 1)
  : RefreshingCache[K, V] = {
    val reloadPeriod_ = reloadPeriod
    val retrieveAll_ = retrieveAll
    val maxAttempts_ = maxAttempts
    new RefreshingCache[K, V]() {
      override def scheduler: Scheduler = app.actorSystem.scheduler
      override def retrieveExecutionContext = Implicits.global
      override def reloadPeriod = reloadPeriod_
      override def retrieveAll = retrieveAll_()
      override def maxAttempts: Int = maxAttempts_
    }
  }

  "RefreshingCache" should "load and get" in {
    val cache = createCache[String, Int](1.minute, () => Map("1" -> 1, "2" -> 2))

    cache.get("1").value shouldBe 1
    cache.get("2").value shouldBe 2
    cache.get("3") shouldBe None
  }

  it should "fail to initialize if retrieve fails at startup" in {
    val retrieve = () => throw new IllegalStateException("boom")
    an[IllegalStateException] should be thrownBy createCache(1.minute, retrieve)
  }

  it should "refresh data" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("1" -> 1, "2" -> 2))
      .thenReturn(Map("3" -> 3, "4" -> 4))

    val cache = createCache(50.millis, retrieve)

    cache.get("1").value shouldBe 1
    cache.get("2").value shouldBe 2
    cache.get("3") shouldBe None
    cache.get("4") shouldBe None

    eventually(Timeout(100.millis)) {
      cache.get("1") shouldBe None
      cache.get("2") shouldBe None
      cache.get("3").value shouldBe 3
      cache.get("4").value shouldBe 4
    }
  }

  it should "return old data if retrieve fails" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("1" -> 1, "2" -> 2))
      .thenThrow(new IllegalStateException("boom"))

    val cache = createCache(10.millis, retrieve)

    cache.get("1").value shouldBe 1
    cache.get("2").value shouldBe 2

    Thread.sleep(50)

    cache.get("1").value shouldBe 1
    cache.get("2").value shouldBe 2
  }

  it should "retry to retrieve" in {
    val retrieve = mock[() => Map[String, Int]]
    Mockito.when(retrieve.apply())
      .thenReturn(Map("1" -> 1, "2" -> 2))
      .thenThrow(new IllegalStateException("boom"))
      .thenReturn(Map("3" -> 3, "4" -> 4))

    val cache = createCache(50.millis, retrieve, maxAttempts = 3)

    cache.get("1").value shouldBe 1
    cache.get("2").value shouldBe 2

    eventually(Timeout(80.millis)) {
      cache.get("1") shouldBe None
      cache.get("2") shouldBe None
      cache.get("3").value shouldBe 3
      cache.get("4").value shouldBe 4
    }
  }

}
