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
  * executes the refresh function then)
  */
trait Cache[K, V] {

  private[this] val cache = new java.util.concurrent.ConcurrentHashMap[K, CacheEntry[V]]()

  /**
    * Defines how long to cache each value for
    */
  val duration: FiniteDuration = FiniteDuration(1, MINUTES)

  def refresh(key: K): V

  def flush(key: K): Unit = {
    cache.remove(key)
  }

  def get(key: K): V = {
    Option(cache.get(key)).filterNot(_.isExpired).map(_.value).getOrElse {
      Try {
        refresh(key)
      } match {
        case Success(v) => {
          cache.put(
            key,
            CacheEntry(
              v,
              expiresAt = DateTime.now.plusSeconds(duration.toSeconds.toInt)
            )
          )
          v
        }

        case Failure(ex) => {
          val msg = s"FlowError for Cache[${this.getClass.getName}] key[$key]: ${ex.getMessage}"
          Logger.error(msg, ex)
          sys.error(msg)
        }
      }
    }
  }

}