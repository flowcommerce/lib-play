package io.flow.play.util

import io.flow.log.RollbarProvider
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import scala.concurrent.Future
import scala.concurrent.duration.{FiniteDuration, _}

class RefreshingBulkCacheAsyncSpec extends AnyWordSpec with GuiceOneAppPerSuite with Matchers with OptionValues {

  private[this] val logger = RollbarProvider.logger("test")

  def createCache[K, V](
    reloadPeriod: FiniteDuration,
    retrieve: () => Future[Map[K, V]],
    maxAttempts: Int = 1
  ): RefreshingBulkCacheAsync[K, V] =
    RefreshingBulkCacheAsync.fromActorSystem(retrieve, app.actorSystem, logger, reloadPeriod, maxAttempts)

  "RefreshingBulkCacheAsync" should {

    "retrieve and get" in {
      val cache = createCache[String, Int](1.minute, () => Future.successful(Map("1" -> 1)))

      cache.get shouldBe Map("1" -> 1)
      cache.getKey("1").value shouldBe 1
      cache.getKey("2") shouldBe None
    }

  }

}
