package io.flow.play.util

import org.joda.time.DateTime
import play.api.Logger

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

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
trait CacheWithFallbackToStaleData[K, V] {

  private[this] val cache = new java.util.concurrent.ConcurrentHashMap[K, CacheEntry[V]]()

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
    Option(cache.get(key)) match {
      case Some(entry) if !entry.isExpired => {
        cache.put(key, entry.copy(expiresAt = DateTime.now.minusSeconds(1)))
      }
      case _ => // no-op
    }
  }

  def get(key: K): V = {
    Option(cache.get(key)) match {
      case Some(entry) if !entry.isExpired => entry.value

      case Some(staleEntry) => {
        doRefresh(key) { ex =>
          Logger.warn(s"FlowError: Cache[${this.getClass.getName}] key[$key]: Falling back to stale data as refresh failed with: ${ex.getMessage}", ex)
          staleEntry.value
        }
      }

      case None => {
        doRefresh(key) { ex =>
          val msg = s"FlowError for Cache[${this.getClass.getName}] key[$key]: ${ex.getMessage}"
          Logger.error(msg, ex)
          sys.error(msg)
        }
      }
    }
  }

  private[this] def doRefresh(key: K)(
    failureFunction: Throwable => V
  ): V = {
    Try {
      refresh(key)
    } match {
      case Success(value) => {
        cache.put(
          key,
          CacheEntry(
            value = value,
            expiresAt = DateTime.now.plusSeconds(duration.toSeconds.toInt)
          )
        )
        value
      }

      case Failure(ex) => {
        failureFunction(ex)
      }
    }
  }

}
