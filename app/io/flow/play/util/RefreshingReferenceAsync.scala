package io.flow.play.util

import java.time.ZonedDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

import akka.actor.{ActorSystem, Scheduler}
import io.flow.log.RollbarLogger

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Maintains a reference that is refreshed asynchronously every `reloadInterval`.
  *
  * The data is asynchronously refreshed every `reloadInterval` period calling the `retrieve` function up to
  * `maxAtttempts` times if the function throws an exception or is completed by a failure.
  * Upon successful completion, the cache is refreshed with the retrieved data, otherwise the cache is not refreshed
  * and will retry `reloadInterval` after the first failed attempt.
  *
  * The cache will throw an exception on creation if the retrieval function fails the first `maxAttempts` times to avoid
  * querying a cache that has never been initialized.
  *
  * If the asynchronous `retrieve` function has not completed when the next call is scheduled, this next call is not
  * issued.
  *
  * The `forceRefresh` will always issue a call to the `retrieve` function, regardless of scheduled calls or other
  * `forceRefresh` not yet completed.
  * Analogously to a scheduled refresh, the cache is refreshed with the retrieved data when the call completes.
  *
  * Example usage:
  *
  * {{{
  *   val cache = RefreshingReferenceAsync.fromActorSystem[Seq[Feature]](
  *     retrieve = () => client.getAllFeatures,
  *     system = system,
  *     logger = logger,
  *     reloadInterval = 2.minutes
  *   )
  * }}}
  *
  */
trait RefreshingReferenceAsync[T] {

  def logger: RollbarLogger

  private[this] def log: RollbarLogger = logger.withKeyValue("class", getClass.getName)

  /**
    * The scheduler to use to schedule the [[retrieve]] function every [[reloadInterval]] period.
    */
  def scheduler: Scheduler

  /**
    * The context to use to execute the [[retrieve]] function every [[reloadInterval]] period.
    */
  def retrieveExecutionContext: ExecutionContext

  /**
    * Interval between refreshes
    */
  def reloadInterval: FiniteDuration

  /**
    * Function retuning the data to cache.
    * It is called every [[reloadInterval]] period.
    * If successful, the cache is refreshed with the latest data, otherwise the cache is not refreshed.
    */
  def retrieve: Future[T]

  /**
    * Maximum number of attempts to retrieve the data.
    * If the [[retrieve]] function fails [[maxAttempts]] times in a row, the cache will not refresh and will retry
    * to retrieve data after [[reloadInterval]].
    *
    * Default: 3
    */
  def maxAttempts: Int = 3

  /**
    * Forces the cache to refresh. If successful, the cache is refreshed with the latest data, otherwise the cache is not refreshed.
    *
    * @return the result of the refresh call, potentially a failed [[Future]]
    */
  def forceRefresh(): Future[T] = refreshInternal(force = true).map(_.value)(retrieveExecutionContext)

  def get: T = cache.get().value

  def shutdown(): Boolean = scheduled.cancel()

  // keep track of non completed retrievals
  private[this] val retrieving = new ConcurrentHashMap[Object, Future[AsyncResult[T]]]()

  // load blocking and fail if the retrieval is not successful after maxAttempts
  private[this] val cache: AtomicReference[AsyncResult[T]] =
    Try(Await.result(doLoadRetry(1, maxAttempts), 10.seconds)) match {
      case Success(data) =>
        new AtomicReference(data)
      case Failure(ex) =>
        log
          .withKeyValue("max_attempts", maxAttempts)
          .warn("Failed to initialize cache", ex)
        throw ex
    }

  // schedule subsequent reloads
  private val scheduled = scheduler.scheduleAtFixedRate(reloadInterval, reloadInterval) { () =>
    refreshInternal(force = false)
    ()
  }(retrieveExecutionContext)

  private def refreshInternal(force: Boolean): Future[AsyncResult[T]] =
    if (force)
      doRefresh()
    else
    // synchronize to avoid fetching twice if the map is empty
      retrieving.synchronized {
        if (retrieving.isEmpty)
          doRefresh()
        else
          Future.successful(cache.get)
      }

  private def doRefresh(): Future[AsyncResult[T]] = {
    val key = new Object
    val res = retrieving.compute(key, (_, _) => doLoadRetry(1, maxAttempts))

    // update cache
    res.onComplete {
      case Success(data) =>
        // only set if requested at is after
        cache.updateAndGet { current =>
          if (data.requestedAt.isAfter(current.requestedAt)) data
          else current
        }
      case Failure(ex) =>
        log
          .withKeyValue("max_attempts", maxAttempts)
          .withKeyValue("reload_interval", reloadInterval.toString)
          .warn(s"Failed to refresh cache. Will try again in $reloadInterval", ex)
    }(retrieveExecutionContext)

    // removing from fetching
    res.onComplete(_ => retrieving.remove(key))(retrieveExecutionContext)

    res
  }

  private def doLoadRetry(attempts: Int, maxAttempts: Int): Future[AsyncResult[T]] = {
    val requestedAt = ZonedDateTime.now()
    // in case the provided retrieve throws an exception, enclose in a Future
    // also allows for creating a Future right away
    Future
      .unit
      .flatMap(_ =>  retrieve)(retrieveExecutionContext)
      .map(f => AsyncResult(requestedAt, f))(retrieveExecutionContext)
      .recoverWith { case ex if attempts < maxAttempts =>
        log.
          withKeyValue("max_attempts", maxAttempts).
          withKeyValue("reload_interval", reloadInterval.toString).
          info(s"Failed to refresh cache ($attempts/$maxAttempts). Trying again...", ex)
        doLoadRetry(attempts + 1, maxAttempts)
      }(retrieveExecutionContext)
  }

}

private case class AsyncResult[T](
  requestedAt: java.time.ZonedDateTime,
  value: T
)

object RefreshingReferenceAsync {

  /**
    * Helper function to create a new [[RefreshingReference]]
    */
  def apply[T](
    retrieve: () => Future[T],
    scheduler: Scheduler,
    retrieveExecutionContext: ExecutionContext,
    rollbarLogger: RollbarLogger,
    reloadInterval: FiniteDuration = 1.minute,
    maxAttempts: Int = 3,
  ): RefreshingReferenceAsync[T] = {
    val schedulerOuter = scheduler
    val retrieveExecutionContextOuter = retrieveExecutionContext
    val reloadIntervalOuter = reloadInterval
    val retrieveOuter = retrieve
    val maxAttemptsOuter = maxAttempts
    new RefreshingReferenceAsync[T]() {
      override def logger: RollbarLogger = rollbarLogger
      override def scheduler: Scheduler = schedulerOuter
      override def retrieveExecutionContext: ExecutionContext = retrieveExecutionContextOuter
      override def reloadInterval: FiniteDuration = reloadIntervalOuter
      override def retrieve: Future[T] = retrieveOuter()
      override def maxAttempts: Int = maxAttemptsOuter
    }
  }

  /**
    * Helper function to create a new [[RefreshingReference]]
    *
    * Uses the system's scheduler and default dispatcher
    */
  def fromActorSystem[T](
    retrieve: () => Future[T],
    system: ActorSystem,
    rollbarLogger: RollbarLogger,
    reloadInterval: FiniteDuration = 1.minute,
    maxAttempts: Int = 3
  ): RefreshingReferenceAsync[T] = apply(
    retrieve = retrieve,
    scheduler = system.scheduler,
    retrieveExecutionContext = system.dispatcher,
    reloadInterval = reloadInterval,
    rollbarLogger = rollbarLogger,
    maxAttempts = maxAttempts
  )

}