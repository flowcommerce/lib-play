package io.flow.play.util

import akka.actor.{ActorSystem, Scheduler}
import io.flow.akka.actor.ManagedShutdown
import io.flow.log.RollbarLogger
import io.flow.util.{CacheStatsRecorder, HasCacheStatsRecorder, NoOpCacheStatsRecorder, Shutdownable}

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.chaining.scalaUtilChainingOps
import scala.util.{Failure, Success, Try}

/** Maintains a reference that gets refreshed every `reloadInterval`
  *
  * The data is refreshed every `reloadInterval` period calling the `retrieve` function up to `maxAtttempts` times Upon
  * successful completion, the data is refreshed with the retrieved data, otherwise the cache is not refreshed.
  *
  * The class will not initialize if the retrieval function fails the first `maxAttempts` times to avoid querying a
  * cache that has never been initialized.
  */
trait RefreshingReference[T] extends Shutdownable with NoOpCacheStatsRecorder {
  self: HasCacheStatsRecorder =>

  def logger: RollbarLogger

  private[this] def log: RollbarLogger = logger.withKeyValue("class", getClass.getName)

  /** The scheduler to use to schedule the [[retrieve]] function every [[reloadInterval]] period.
    */
  def scheduler: Scheduler

  /** The context to use to execute the [[retrieve]] function every [[reloadInterval]] period.
    */
  def retrieveExecutionContext: ExecutionContext

  /** Interval between refreshes
    */
  def reloadInterval: FiniteDuration

  /** Function retuning the data to cache. It is called every [[reloadInterval]] period. If successful, the cache is
    * refreshed with the latest data, otherwise the cache is not refreshed.
    */
  def retrieve: T

  /** Maximum number of attempts to retrieve the data. If the [[retrieve]] function fails [[maxAttempts]] times in a
    * row, the cache will not refresh and will retry to retrieve data after [[reloadInterval]].
    *
    * Default: 3
    */
  def maxAttempts: Int = 3

  /** Forces the cache to refresh. If successful, the cache is refreshed with the latest data, otherwise the cache is
    * not refreshed.
    *
    * @return
    *   true if cache is successfully refreshed and false otherwise
    */
  def forceRefresh(): Boolean = {
    doLoadRetry(1, maxAttempts) match {
      case Success(data) =>
        cache.set(data).tap(_ => cacheStatsRecorder.recordRemoval(CacheStatsRecorder.RemovalReason.Explicit))
        true
      case Failure(ex) =>
        log.withKeyValue("max_attempts", maxAttempts).warn("Failed to initialize cache", ex)
        false
    }
  }

  def get: T = cache.get().tap(_ => cacheStatsRecorder.recordHits(1L))

  // load blocking and fail if the retrieval is not successful after maxAttempts
  private[this] val cache: AtomicReference[T] = {
    val retrieved = doLoadRetry(1, maxAttempts) match {
      case Success(data) => data
      case Failure(ex) =>
        log.withKeyValue("max_attempts", maxAttempts).warn("Failed to initialize cache", ex)
        throw ex
    }
    cacheStatsRecorder.recordMisses(1L) // First get is a miss
    new AtomicReference(retrieved)
  }

  // schedule subsequent reloads
  scheduler.scheduleWithFixedDelay(reloadInterval, reloadInterval) { () =>
    if (isShutdown) {
      log.info("Not refreshing reference, shutdown in progress")
    } else {
      doLoadRetry(1, maxAttempts) match {
        case Success(data) =>
          cache
            .set(data)
            .tap(_ => cacheStatsRecorder.recordRemoval(CacheStatsRecorder.RemovalReason.Expired))
        case Failure(ex) =>
          log
            .withKeyValue("max_attempts", maxAttempts)
            .withKeyValue("reload_interval", reloadInterval.toString)
            .warn("Failed to refresh cache. Will try again", ex)
      }
    }
  }(retrieveExecutionContext)

  private def doLoadRetry(attempts: Int, maxAttempts: Int): Try[T] =
    Success(System.nanoTime()).flatMap { startTime =>
      Try(retrieve)
        .tap {
          case Failure(_) => cacheStatsRecorder.recordLoadFailure(System.nanoTime() - startTime)
          case Success(_) => cacheStatsRecorder.recordLoadSuccess(System.nanoTime() - startTime)
        }
        .recoverWith {
          case ex if attempts < maxAttempts =>
            log
              .withKeyValue("max_attempts", maxAttempts)
              .withKeyValue("reload_interval", reloadInterval.toString)
              .info("Failed to refresh cache. Trying again...", ex)
            doLoadRetry(attempts + 1, maxAttempts)
        }
    }

}

abstract class ShutdownManangedRefreshingReference[T](override val system: ActorSystem)
  extends RefreshingReference[T]
  with HasCacheStatsRecorder
  with ManagedShutdown {}

object RefreshingReference {

  /** Helper function to create a new [[RefreshingReference]]
    */
  def apply[T](
    rollbarLogger: RollbarLogger,
    scheduler: Scheduler,
    retrieveExecutionContext: ExecutionContext,
    reloadInterval: FiniteDuration,
    retrieve: () => T,
    maxAttempts: Int = 3,
    statsRecorder: CacheStatsRecorder = CacheStatsRecorder.NoOpCacheStatsRecorder,
  ): RefreshingReference[T] = {
    val schedulerOuter = scheduler
    val retrieveExecutionContextOuter = retrieveExecutionContext
    val reloadIntervalOuter = reloadInterval
    val retrieveOuter = retrieve
    val maxAttemptsOuter = maxAttempts
    new RefreshingReference[T]() with HasCacheStatsRecorder {
      override def logger: RollbarLogger = rollbarLogger
      override def scheduler: Scheduler = schedulerOuter
      override def retrieveExecutionContext: ExecutionContext = retrieveExecutionContextOuter
      override def reloadInterval: FiniteDuration = reloadIntervalOuter
      override def retrieve: T = retrieveOuter()
      override def maxAttempts: Int = maxAttemptsOuter
      override def cacheStatsRecorder: CacheStatsRecorder = statsRecorder
    }
  }

  // Generated caches use this
  def apply[T](
    rollbarLogger: RollbarLogger,
    scheduler: Scheduler,
    retrieveExecutionContext: ExecutionContext,
    reloadInterval: FiniteDuration,
    actorSystem: ActorSystem,
    retrieve: () => T,
    maxAttempts: Int,
  ): RefreshingReference[T] =
    forActorSystem(
      rollbarLogger,
      scheduler,
      retrieveExecutionContext,
      reloadInterval,
      actorSystem,
      retrieve,
      maxAttempts,
      statsRecorder = CacheStatsRecorder.NoOpCacheStatsRecorder,
    )

  def forActorSystem[T](
    rollbarLogger: RollbarLogger,
    scheduler: Scheduler,
    retrieveExecutionContext: ExecutionContext,
    reloadInterval: FiniteDuration,
    actorSystem: ActorSystem,
    retrieve: () => T,
    maxAttempts: Int,
    statsRecorder: CacheStatsRecorder,
  ): RefreshingReference[T] = {
    val schedulerOuter = scheduler
    val retrieveExecutionContextOuter = retrieveExecutionContext
    val reloadIntervalOuter = reloadInterval
    val retrieveOuter = retrieve
    val maxAttemptsOuter = maxAttempts
    new ShutdownManangedRefreshingReference[T](actorSystem) {
      override def logger: RollbarLogger = rollbarLogger
      override def scheduler: Scheduler = schedulerOuter
      override def retrieveExecutionContext: ExecutionContext = retrieveExecutionContextOuter
      override def reloadInterval: FiniteDuration = reloadIntervalOuter
      override def retrieve: T = retrieveOuter()
      override def maxAttempts: Int = maxAttemptsOuter
      override def cacheStatsRecorder: CacheStatsRecorder = statsRecorder
    }
  }
}
