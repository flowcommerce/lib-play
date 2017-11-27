package io.flow.play.util

import java.util.concurrent.ConcurrentHashMap

import akka.actor.Scheduler
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

trait RefreshingCache[K, V] {

  @volatile
  private[this] var cache: ConcurrentHashMap[K, V] = new ConcurrentHashMap()

  def scheduler: Scheduler
  def retrieveExecutionContext: ExecutionContext
  def reloadPeriod: FiniteDuration
  def retrieveAll: Map[K, V]
  def maxAttempts: Int = 3

  def get(key: K): Option[V] = Option(cache.get(key))

  // load blocking and fail if the retrieval is not successful after 3 attempts
  doLoadRetry(1, maxAttempts).get
  // schedule subsequent reloads
  scheduler.schedule(reloadPeriod, reloadPeriod)(doLoadRetryRecover(1, maxAttempts))(retrieveExecutionContext)

  private def doLoadRetryRecover(attempts: Int, maxAttempts: Int): Try[Unit] = {
    doLoadRetry(attempts, maxAttempts)
      .recover { case ex =>
        Logger.warn(s"Failed refreshing cache at final attempt $attempts/$maxAttempts. " +
          s"Will try again in $reloadPeriod", ex)
      }
  }

  private def doLoadRetry(attempts: Int, maxAttempts: Int): Try[Unit] = {
    Try(retrieveAll)
      .map(all => cache = new ConcurrentHashMap[K, V](all.asJava))
      .recoverWith { case ex if attempts < maxAttempts =>
        Logger.warn(s"Failed refreshing cache at attempt $attempts/$maxAttempts. Trying again...", ex)
        doLoadRetry(attempts + 1, maxAttempts)
      }
  }

}
