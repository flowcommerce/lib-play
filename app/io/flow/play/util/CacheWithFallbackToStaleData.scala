package io.flow.play.util

import java.util.function.BiFunction

import io.flow.log.RollbarLogger
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
private[util] case class CacheEntry[V](
  value: V,
  expiresAt: DateTime
) {

  def isExpired: Boolean = expiresAt.isBeforeNow

}

/**
  * Caches data for a short period of time (configurable, defaults to 1 minute)
  *
  * Refreshes data on demand (when you call `get`, if entry is not in cache
  * executes the refresh function then). If the call to `get` failes, and
  * and there is data cached in memory, you will get back the stale data.
  */
@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
trait CacheWithFallbackToStaleData[K, V] {

  private[this] val cache = new java.util.concurrent.ConcurrentHashMap[K, CacheEntry[V]]()

  def logger: RollbarLogger

  private[this] def log: RollbarLogger = logger.withKeyValue("class", getClass.getName)

  /**
    * Defines how long to cache each value for
    */
  val duration: FiniteDuration = FiniteDuration(1, MINUTES)

  def refresh(key: K): V

  /**
    * Marks the specified key as expired. On next access, will attempt to refresh. Note that
    * if refresh fails, we will continue to return the stale data.
    */
  def flush(key: K): Unit = {
    cache.computeIfPresent(key, new BiFunction[K, CacheEntry[V], CacheEntry[V]] {
      override def apply(k: K, entry: CacheEntry[V]): CacheEntry[V] = entry.copy(expiresAt = DateTime.now.minusMillis(1))
    })
    ()
  }
  def get(key: K): V = {
    // try to do a quick get first
    val finalEntry = Option(cache.get(key)) match {
      case Some(retrievedEntry) =>
        if (!retrievedEntry.isExpired) retrievedEntry
        else {
          // atomically compute a new entry, to avoid calling "refresh" multiple times
          cache.compute(key, new BiFunction[K, CacheEntry[V], CacheEntry[V]] {
            override def apply(k: K, currentEntry: CacheEntry[V]): CacheEntry[V] = {
              Option(currentEntry) match {
                // check again as this value may have been updated by a concurrent call
                case Some(foundEntry) =>
                  if (!foundEntry.isExpired) foundEntry
                  else doGetEntry(k)(_ => failureFromRefresh(k, foundEntry))
                case None => doGetEntry(k)(failureFromEmpty(k, _))
              }
            }
          })
        }
      // compute if absent as this value may have been updated by a concurrent call
      case None => cache.computeIfAbsent(key, new java.util.function.Function[K, CacheEntry[V]] {
        override def apply(k: K): CacheEntry[V] = doGetEntry(k)(failureFromEmpty(k, _))
      })
    }
    finalEntry.value
  }

  private[this] def failureFromEmpty(key: K, ex: Throwable): CacheEntry[V] = {
    log.
      withKeyValue("key", key.toString).
      error("failureFromEmpty", ex)
    sys.error(s"failureFromEmpty for key[$key]")
  }

  private[this] def failureFromRefresh(key: K, currentEntry: CacheEntry[V]): CacheEntry[V] = {
    log.
      withKeyValue("key", key.toString).
      warn("failureFromRefresh - Falling back to stale data")
    currentEntry
  }

  private[this] def doGetEntry(key: K)(
    failureFunction: Throwable => CacheEntry[V]
  ): CacheEntry[V] = {
    Try(refresh(key)) match {
      case Success(value) => CacheEntry(value = value, expiresAt = DateTime.now.plusSeconds(duration.toSeconds.toInt))
      case Failure(ex) => failureFunction(ex)
    }
  }

}
