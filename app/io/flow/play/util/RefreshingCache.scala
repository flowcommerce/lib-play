package io.flow.play.util

import java.util.concurrent.atomic.AtomicReference

import akka.actor.Scheduler
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

/**
  * Cache to store data in bulk.
  *
  * The data is refreshed every `reloadInterval` period calling the `retrieveAll` function up to `maxAtttempts` times
  * Upon successful completion, the data is refreshed with the retrieved data, otherwise the cache is not refreshed.
  *
  * The class will not initialize if the retrieval function fails the first `maxAttempts` times to avoid querying a
  * cache that has never been initialized.
  */
trait RefreshingCache[K, V] {

  /**
    * The scheduler to use to schedule the [[retrieveAll]] function every [[reloadInterval]] period.
    */
  def scheduler: Scheduler

  /**
    * The context to use to execute the [[retrieveAll]] function every [[reloadInterval]] period.
    */
  def retrieveExecutionContext: ExecutionContext

  /**
    * Amount 
    */
  def reloadInterval: FiniteDuration

  /**
    * Function retuning the data to cache. 
    * It is called every [[reloadInterval]] period. 
    * If successful, the cache is refreshed with the latest data, otherwise the cache is not refreshed.
    */
  def retrieveAll: Map[K, V]

  /**
    * Maximum number of attempts to retrieve the data.
    * If the [[retrieveAll]] function fails [[maxAttempts]] times in a row, the cache will not refresh and will retry
    * to retrieve data after [[reloadInterval]].
    *
    * Default: 3
    */
  def maxAttempts: Int = 3
  
  def get(key: K): Option[V] = cache.get().get(key)
  def asMap: Map[K, V] = cache.get()

  // load blocking and fail if the retrieval is not successful after maxAttempts
  private[this] val cache: AtomicReference[Map[K, V]] = {
    val retrieved = doLoadRetry(1, maxAttempts) match {
      case Success(data) => data
      case Failure(ex) =>
        Logger.warn(s"Failed initializing cache after $maxAttempts attempts", ex)
        throw ex
    }
    new AtomicReference(retrieved)
  }

  // schedule subsequent reloads
  scheduler.schedule(reloadInterval, reloadInterval) {
    doLoadRetry(1, maxAttempts) match {
      case Success(data) => cache.set(data)
      case Failure(ex) => Logger.warn(s"Failed refreshing cache after $maxAttempts attempts. " +
        s"Will try again in $reloadInterval", ex)
    }
  }(retrieveExecutionContext)

  private def doLoadRetry(attempts: Int, maxAttempts: Int): Try[Map[K, V]] =
    Try(retrieveAll)
      .recoverWith { case ex if attempts < maxAttempts =>
        Logger.warn(s"Failed refreshing cache at attempt $attempts/$maxAttempts. Trying again...", ex)
        doLoadRetry(attempts + 1, maxAttempts)
      }

}

object RefreshingCache {

  /**
    * Helper function to create a new [[RefreshingCache]]
    */
  def apply[K, V](scheduler: Scheduler, retrieveExecutionContext: ExecutionContext, reloadInterval: FiniteDuration,
                  retrieveAll: () => Map[K,V], maxAttempts: Int = 3): RefreshingCache[K, V] = {
    val schedulerOuter = scheduler
    val retrieveExecutionContextOuter = retrieveExecutionContext
    val reloadIntervalOuter = reloadInterval
    val retrieveAllOuter = retrieveAll
    val maxAttemptsOuter = maxAttempts
    new RefreshingCache[K, V]() {
      override def scheduler: Scheduler = schedulerOuter
      override def retrieveExecutionContext: ExecutionContext = retrieveExecutionContextOuter
      override def reloadInterval: FiniteDuration = reloadIntervalOuter
      override def retrieveAll: Map[K, V] = retrieveAllOuter()
      override def maxAttempts: Int = maxAttemptsOuter
    }
  }

}
