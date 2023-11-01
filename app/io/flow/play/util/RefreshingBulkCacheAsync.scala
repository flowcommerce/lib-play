package io.flow.play.util

import akka.actor.{ActorSystem, Scheduler}
import io.flow.log.RollbarLogger

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future}

/** Example:
  *
  * {{{
  * val cache = RefreshingReferenceAsync.fromActorSystem[String, String](
  *   retrieve = () => client.getAllFeatures.map(features.groupBy(_.organizationId).mapValues(_.featureKey)),
  *   system = system,
  *   logger = logger,
  *   reloadInterval = 2.minutes
  * )
  * // ...
  * val hasFeature: Boolean = cache.getKey("org").contains("some_feature")
  * }}}
  */
trait RefreshingBulkCacheAsync[K, V] extends RefreshingReferenceAsync[Map[K, V]] {

  def getKey(key: K): Option[V] = super.get.get(key)

}

object RefreshingBulkCacheAsync {

  /** Helper function to create a new [[RefreshingReference]]
    */
  def apply[K, V](
    retrieve: () => Future[Map[K, V]],
    scheduler: Scheduler,
    retrieveExecutionContext: ExecutionContext,
    rollbarLogger: RollbarLogger,
    reloadInterval: FiniteDuration = 1.minute,
    maxAttempts: Int = 3
  ): RefreshingBulkCacheAsync[K, V] = {
    val schedulerOuter = scheduler
    val retrieveExecutionContextOuter = retrieveExecutionContext
    val reloadIntervalOuter = reloadInterval
    val retrieveOuter = retrieve
    val maxAttemptsOuter = maxAttempts
    new RefreshingBulkCacheAsync[K, V]() {
      override def logger: RollbarLogger = rollbarLogger
      override def scheduler: Scheduler = schedulerOuter
      override def retrieveExecutionContext: ExecutionContext = retrieveExecutionContextOuter
      override def reloadInterval: FiniteDuration = reloadIntervalOuter
      override def retrieve: Future[Map[K, V]] = retrieveOuter()
      override def maxAttempts: Int = maxAttemptsOuter
    }
  }

  /** Helper function to create a new [[RefreshingReference]]
    *
    * Uses the system's scheduler and default dispatcher
    */
  def fromActorSystem[K, V](
    retrieve: () => Future[Map[K, V]],
    system: ActorSystem,
    rollbarLogger: RollbarLogger,
    reloadInterval: FiniteDuration = 1.minute,
    maxAttempts: Int = 3
  ): RefreshingBulkCacheAsync[K, V] = apply(
    retrieve = retrieve,
    scheduler = system.scheduler,
    retrieveExecutionContext = system.dispatcher,
    reloadInterval = reloadInterval,
    rollbarLogger = rollbarLogger,
    maxAttempts = maxAttempts
  )

}
