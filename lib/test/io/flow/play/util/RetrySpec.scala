package io.flow.play.util

import org.mockito.Mockito._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.concurrent.Futures

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class RetryFuturesSpec extends LibPlaySpec with ScalaFutures with MockitoSugar {

  private val retryFutures = app.injector.instanceOf[RetryFutures]
  implicit private val ec: ExecutionContext = app.actorSystem.dispatcher
  private val futures = app.injector.instanceOf[Futures]

  def config(
    delay: FiniteDuration = 10.millis,
    maxAttempts: Int = 5,
    overallTimeout: Option[FiniteDuration] = None,
    forExceptions: PartialFunction[Throwable, Unit] = PartialFunction.empty,
  ): RetryConfiguration =
    RetryConfiguration
      .withFixedDelay(delay, maxAttempts = maxAttempts, overallTimeout = overallTimeout, forExceptions = forExceptions)

  "RetryFutures" should {

    "retry and succeed after matching errors" in {
      val f = mock[() => Future[Int]]
      when(f.apply())
        .thenReturn(Future.failed(MyException(429)))
        .thenReturn(Future.failed(MyException(501)))
        .thenReturn(Future.failed(new IllegalArgumentException("retryable")))
        .thenReturn(Future.successful(1))

      val conf = config(maxAttempts = 10)
        .onExceptions {
          case MyException(429) => ()
          case MyException(501) => ()
        }
        .onExceptions[IllegalArgumentException]

      retryFutures.run(conf)(f.apply()).futureValue(Timeout(1.second)) mustBe 1
    }

    "fail after non-matching error" in {
      val f = mock[() => Future[Int]]
      when(f.apply())
        .thenReturn(Future.failed(MyException(429)))
        .thenReturn(Future.failed(MyException(404)))

      val conf = config(maxAttempts = 10).onExceptions { case MyException(429) => () }

      retryFutures.run(conf)(f.apply()).failed.futureValue(Timeout(1.second)) mustBe a[MyException]
    }

    "fail after max attempts exceeded" in {
      val f = mock[() => Future[Int]]
      val max = 3
      (1 to max).foreach(_ => when(f.apply()).thenReturn(Future.failed(new IllegalArgumentException("retryable"))))

      val conf = config(maxAttempts = max)
        .onExceptions[IllegalArgumentException]

      retryFutures.run(conf)(f.apply()).failed.futureValue(Timeout(1.second)) mustBe a[IllegalArgumentException]
    }

    "fail after overall timeout exceeded" in {
      val f = mock[() => Future[Int]]
      (1 to 4).foreach(_ => when(f.apply()).thenReturn(Future.failed(new IllegalArgumentException)))

      val conf = config(delay = 80.millis, maxAttempts = 100, overallTimeout = Some(200.millis))
        .onExceptions[IllegalArgumentException]

      retryFutures.run(conf)(f.apply()).failed.futureValue(Timeout(1.second)) mustBe a[IllegalArgumentException]
    }

    "retry once when first attempt exceeds timeout" in {
      val f = mock[() => Future[Int]]

      when(f.apply())
        .thenReturn(futures.delayed(200.millis)(Future.successful(1))) // simulate slow Future
        .thenReturn(Future.successful(42)) // Succeeds on retry

      val conf = RetryConfiguration
        .withFixedDelay(delay = 10.millis, maxAttempts = 2)
        .withTimeout(Some(50.millis)) // This will trigger a TimeoutException

      val result = retryFutures.run(conf)(f.apply()).futureValue(Timeout(1.second))

      result mustBe 42
      verify(f, times(2)).apply() // Confirm exactly 2 calls (1 timeout + 1 retry)
    }

    "retry and succeed before overall timeout" in {
      val f = mock[() => Future[Int]]
      when(f.apply())
        .thenReturn(Future.failed(new IllegalArgumentException))
        .thenReturn(Future.failed(new IllegalArgumentException))
        .thenReturn(Future.successful(42))

      val conf = config(delay = 50.millis, maxAttempts = 10, overallTimeout = Some(200.millis))
        .onExceptions[IllegalArgumentException]

      retryFutures.run(conf)(f.apply()).futureValue(Timeout(1.second)) mustBe 42
    }

    "retry with exponential backoff and succeed" in {
      val f = mock[() => Future[Int]]

      // Always fail for first 3 attempts, then succeed
      when(f.apply())
        .thenReturn(Future.failed(new IllegalArgumentException("boom 1")))
        .thenReturn(Future.failed(new IllegalArgumentException("boom 2")))
        .thenReturn(Future.failed(new IllegalArgumentException("boom 3")))
        .thenReturn(Future.successful(99))

      val baseDelay = 10.millis

      val conf = RetryConfiguration
        .withExponentialBackoff(baseDelay, factorDelay = 2L, maxAttempts = 5)
        .onExceptions[IllegalArgumentException]

      val result = retryFutures.run(conf)(f.apply()).futureValue(Timeout(1.second))

      result mustBe 99

      // Verify we called the function 4 times
      verify(f, times(4)).apply()
    }

  }
}

case class MyException(status: Int) extends Exception
