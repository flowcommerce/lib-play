package io.flow.play.util

import io.flow.log.RollbarProvider
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, OptionValues, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.Future
import scala.concurrent.duration._

class RefreshingReferenceAsyncSpec extends WordSpec with GuiceOneAppPerSuite with Matchers with OptionValues
  with MockitoSugar with Eventually {

  private[this] val logger = RollbarProvider.logger("test")

  def createCache[K, V](
    reloadPeriod: FiniteDuration,
    retrieve: () => Future[Map[K, V]],
    maxAttempts: Int = 1
  ): RefreshingReferenceAsync[Map[K, V]] =
    RefreshingReferenceAsync(logger, app.actorSystem.scheduler, Implicits.global, reloadPeriod, retrieve, maxAttempts)

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

  }

}
