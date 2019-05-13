package io.flow.play.util

import io.flow.log.RollbarProvider
import org.mockito.Mockito
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{EitherValues, Matchers, OptionValues, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.concurrent.Futures

import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.libs.concurrent.Futures._

class RefreshingReferenceAsyncSpec extends WordSpec with GuiceOneAppPerSuite with Matchers with OptionValues
  with MockitoSugar with Eventually with ScalaFutures with EitherValues {

  private[this] val logger = RollbarProvider.logger("test")
  implicit private[this] val system = app.actorSystem
  private[this] val futures = implicitly[Futures]

  def createCache[K, V](
    reloadPeriod: FiniteDuration,
    retrieve: () => Future[Map[K, V]],
    maxAttempts: Int = 1
  ): RefreshingReferenceAsync[Map[K, V]] =
    RefreshingReferenceAsync(logger, app.actorSystem.scheduler, app.actorSystem.dispatcher, reloadPeriod, retrieve, maxAttempts)

  "RefreshingReferenceAsync" should {

    "load and get" in {
      val cache = createCache[String, Int](1.minute, () => Future.successful(Map("1" -> 1)))
      cache.get shouldBe Map("1" -> 1)
    }

    "fail to initialize if retrieve fails at startup" in {
      val retrieve = () => throw new IllegalStateException("boom")
      an[IllegalStateException] should be thrownBy createCache(1.minute, retrieve)
    }

    "fail to initialize if retrieved future fails at startup" in {
      val retrieve = () => Future.failed(new IllegalStateException("boom"))
      an[IllegalStateException] should be thrownBy createCache(1.minute, retrieve)
    }

    "refresh data" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1)))
        .thenReturn(futures.delayed(20.millis)(Future.successful(Map("2" -> 2))))

      val cache = createCache(10.millis, retrieve)

      cache.get shouldBe Map("1" -> 1)
      eventually(Timeout(50.millis)) {
        cache.get shouldBe Map("2" -> 2)
      }
    }

    "return old data if retrieve fails" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1)))
        .thenReturn(futures.delayed(20.millis)(Future.failed(new IllegalStateException("boom"))))

      val cache = createCache(10.millis, retrieve)

      cache.get shouldBe Map("1" -> 1)
      Thread.sleep(50)
      cache.get shouldBe Map("1" -> 1)
    }

    "return old data if retrieve throws an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1)))
        .thenThrow(new IllegalStateException("boom"))

      val cache = createCache(10.millis, retrieve)

      cache.get shouldBe Map("1" -> 1)
      Thread.sleep(50)
      cache.get shouldBe Map("1" -> 1)
    }

    "keep retrying if retrieve fails" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenReturn(futures.delayed(10.millis)(Future.failed(new IllegalStateException("boom"))))
        .thenReturn(futures.delayed(20.millis)(Future.successful(Map("3" -> 3, "4" -> 4))))

      val cache = createCache(10.millis, retrieve)

      cache.get shouldBe Map("1" -> 1, "2" -> 2)
      eventually(Timeout(50.millis)) {
        cache.get shouldBe Map("3" -> 3, "4" -> 4)
      }
    }

    "keep retrying if retrieve thows an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenThrow(new IllegalStateException("boom"))
        .thenReturn(futures.delayed(20.millis)(Future.successful(Map("3" -> 3, "4" -> 4))))

      val cache = createCache(10.millis, retrieve)

      cache.get shouldBe Map("1" -> 1, "2" -> 2)
      eventually(Timeout(50.millis)) {
        cache.get shouldBe Map("3" -> 3, "4" -> 4)
      }
    }

    "retry to retrieve when retrieve fails or throws an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenThrow(new IllegalStateException("boom"))
        .thenReturn(futures.delayed(10.millis)(Future.failed(new IllegalStateException("boom"))))
        .thenReturn(futures.delayed(20.millis)(Future.successful(Map("3" -> 3, "4" -> 4))))

      val cache = createCache(30.millis, retrieve, maxAttempts = 3)

      cache.get shouldBe Map("1" -> 1, "2" -> 2)
      eventually(Timeout(50.millis)) {
        cache.get shouldBe Map("3" -> 3, "4" -> 4)
      }
    }

    "force the cache to refresh" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1)))
        .thenReturn(Future.successful(Map("2" -> 2)))

      val cache = createCache(1.day, retrieve)
      cache.get shouldBe Map("1" -> 1)
      val f = cache.forceRefresh()

      eventually(Timeout(100.millis)) {
        cache.get shouldBe Map("2" -> 2)
      }
      // f must be completed
      f.eitherValue.value.right.value shouldBe Map("2" -> 2)
    }

  }

}
