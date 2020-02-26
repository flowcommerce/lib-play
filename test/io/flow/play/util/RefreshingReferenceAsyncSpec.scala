package io.flow.play.util

import akka.actor.ActorSystem
import io.flow.log.RollbarProvider
import org.mockito.Mockito
import org.scalatest.{EitherValues, WordSpec}
import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures._

import scala.concurrent.Future
import scala.concurrent.duration._

class RefreshingReferenceAsyncSpec extends WordSpec with GuiceOneAppPerSuite with Matchers with OptionValues
  with MockitoSugar with Eventually with ScalaFutures with EitherValues {

  private[this] val logger = RollbarProvider.logger("test")
  implicit private[this] val system: ActorSystem = app.actorSystem
  private[this] val futures = implicitly[Futures]

  private def createCache[K, V](
    reloadPeriod: FiniteDuration,
    retrieve: () => Future[Map[K, V]],
    maxAttempts: Int = 1
  ): RefreshingReferenceAsync[Map[K, V]] =
    RefreshingReferenceAsync.fromActorSystem(retrieve, app.actorSystem, logger, reloadPeriod, maxAttempts)

  private def withCache[K,V](
    cache: RefreshingReferenceAsync[Map[K, V]]
  )(
    f: RefreshingReferenceAsync[Map[K, V]] => Assertion
  ): Assertion =
    try {
      f(cache)
    } finally {
      cache.shutdown()
      ()
    }

  private def withCacheInit[K,V](cache: RefreshingReferenceAsync[Map[K, V]]): Assertion =
    withCache(cache)(_ => Succeeded)

  "RefreshingReferenceAsync" should {

    "load and get" in {
      withCache(createCache[String, Int](1.minute, () => Future.successful(Map("1" -> 1)))) { cache =>
      cache.get shouldBe Map("1" -> 1)
      }
    }

    "fail to initialize if retrieve fails at startup" in {
      val retrieve = () => throw new IllegalStateException("boom")
      an[IllegalStateException] should be thrownBy withCacheInit(createCache(1.minute, retrieve))
    }

    "fail to initialize if retrieved future fails at startup" in {
      val retrieve = () => Future.failed(new IllegalStateException("boom"))
      an[IllegalStateException] should be thrownBy withCacheInit(createCache(1.minute, retrieve))
    }

    "refresh data" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1)))
        .thenAnswer(_ => Future.successful(Map("2" -> 2)))

      withCache(createCache(10.millis, retrieve)) { cache =>
        cache.get shouldBe Map("1" -> 1)
        eventually(Timeout(50.millis), Interval(5.millis)) {
          cache.get shouldBe Map("2" -> 2)
        }
      }
    }

    "return old data if retrieve fails" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1)))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))

      withCache(createCache(10.millis, retrieve)) { cache =>
        cache.get shouldBe Map("1" -> 1)
        Thread.sleep(50)
        cache.get shouldBe Map("1" -> 1)
      }
    }

    "return old data if retrieve throws an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1)))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))

      withCache(createCache(10.millis, retrieve)) { cache =>
        cache.get shouldBe Map("1" -> 1)
        Thread.sleep(50)
        cache.get shouldBe Map("1" -> 1)
      }
    }

    "keep retrying if retrieve fails" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))
        .thenAnswer(_ => Future.successful(Map("3" -> 3, "4" -> 4)))

      withCache(createCache(10.millis, retrieve)) { cache =>
        cache.get shouldBe Map("1" -> 1, "2" -> 2)
        eventually(Timeout(100.millis), Interval(5.millis)) {
          cache.get shouldBe Map("3" -> 3, "4" -> 4)
        }
      }
    }

    "keep retrying if retrieve throws an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))
        .thenAnswer(_ => Future.successful(Map("3" -> 3, "4" -> 4)))

      withCache(createCache(10.millis, retrieve)) { cache =>

        cache.get shouldBe Map("1" -> 1, "2" -> 2)
        eventually(Timeout(100.millis), Interval(5.millis)) {
          cache.get shouldBe Map("3" -> 3, "4" -> 4)
        }
      }
    }

    "retry to retrieve when retrieve fails or throws an exception" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1, "2" -> 2)))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))
        .thenAnswer(_ => Future.failed(new IllegalStateException("boom")))
        .thenAnswer(_ => Future.successful(Map("3" -> 3, "4" -> 4)))

      withCache(createCache(10.millis, retrieve, maxAttempts = 3)) { cache =>
        cache.get shouldBe Map("1" -> 1, "2" -> 2)
        eventually(Timeout(50.millis), Interval(5.millis)) {
          cache.get shouldBe Map("3" -> 3, "4" -> 4)
        }
      }
    }

    "force the cache to refresh" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenReturn(Future.successful(Map("1" -> 1)))
        .thenReturn(Future.successful(Map("2" -> 2)))

      withCache(createCache(1.day, retrieve)) { cache =>
        cache.get shouldBe Map("1" -> 1)
        val f = cache.forceRefresh()

        eventually(Timeout(50.millis), Interval(5.millis)) {
          cache.get shouldBe Map("2" -> 2)
        }
        // f must be completed
        f.eitherValue.value shouldBe Right(Map("2" -> 2))
      }
    }

    "fetch at most once at a time" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1)))
        .thenAnswer(_ => futures.delayed(50.millis)(Future.successful(Map("2" -> 2))))

      withCache(createCache(10.millis, retrieve)) { cache =>
        eventually(Timeout(120.millis), Interval(5.millis)) {
          cache.get shouldBe Map("2" -> 2)
          // 1: init
          // 2: first fetch (~50 ms)
          // 3: not completed retrieve
          Mockito.verify(retrieve, Mockito.times(3)).apply()
        }
        Succeeded
      }
    }

    "force the cache to refresh while retrieve in flight" in {
      val retrieve = mock[() => Future[Map[String, Int]]]
      Mockito.when(retrieve.apply())
        .thenAnswer(_ => Future.successful(Map("1" -> 1)))
        .thenAnswer(_ => futures.delayed(70.millis)(Future.successful(Map("2" -> 2))))

      withCache(createCache(10.millis, retrieve)) { cache =>
        eventually(Timeout(30.millis), Interval(5.millis)) {
          cache.get shouldBe Map("1" -> 1)
          // 1: init
          // 2: first fetch (~50 ms)
          Mockito.verify(retrieve, Mockito.times(2)).apply()
        }

        cache.forceRefresh()

        eventually(Timeout(100.millis), Interval(5.millis)) {
          cache.get shouldBe Map("1" -> 1)
          // 1: init
          // 2: first fetch (~50 ms)
          // 3: force refresh
          Mockito.verify(retrieve, Mockito.times(3)).apply()
        }
        Succeeded
      }
    }

  }

}
