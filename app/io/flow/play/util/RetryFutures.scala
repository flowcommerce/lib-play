package io.flow.play.util

import io.flow.log.RollbarLogger
import play.api.libs.concurrent.Futures

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, TimeoutException}
import scala.reflect.ClassTag

@Singleton
class RetryFutures @Inject() (log: RollbarLogger, futures: Futures) {

  /** Retries a Future on specific failures.
    * @param conf
    *   configuration for retrying the Future
    * @param logger
    *   decorated logger to use when retrying, for instance with a fingerprint. If None, the default logger will be
    *   used.
    */
  def run[T](conf: RetryConfiguration, logger: Option[RollbarLogger] = None)(f: => Future[T])(implicit
    ec: ExecutionContext,
  ): Future[T] = {
    val _logger = decorateLogger(logger, conf)
    val deadline = conf.overallTimeout.map(_.fromNow)
    conf.timeout match {
      case Some(t) =>
        // If a timeout is set, we need to create a new configuration for the timeout exception
        val newConf = conf.onExceptions[TimeoutException]
        runRec(newConf, futures.timeout(t)(f), 1, deadline, _logger)
      case None =>
        runRec(conf, f, 1, deadline, _logger)
    }
  }

  private def runRec[A](
    conf: RetryConfiguration,
    f: => Future[A],
    attempt: Int,
    deadline: Option[Deadline],
    logger: RollbarLogger,
  )(implicit ec: ExecutionContext): Future[A] = {
    f.recoverWith {
      case e if conf.forExceptions.isDefinedAt(e) && attempt < conf.maxAttempts && deadline.forall(_.hasTimeLeft()) =>
        val delay = conf.delayPolicy.delay(attempt)
        logRetry(conf.maxAttempts, attempt, delay, logger, e)
        futures.delayed(delay)(runRec(conf, f, attempt + 1, deadline, logger))
      case e =>
        // If we are out of attempts or the deadline has passed, we fail with the last exception
        Future.failed[A](e)
    }
  }

  private def logRetry(
    maxAttempts: Int,
    attempt: Int,
    delay: FiniteDuration,
    logger: RollbarLogger,
    throwable: Throwable,
  ): Unit =
    if (shouldLogAttempt(attempt))
      logger
        .withKeyValue("attempt", attempt)
        .withKeyValue("delay_ms", delay.toMillis)
        .info(
          s"Received error ${throwable.getMessage} after attempt ($attempt/$maxAttempts). " +
            s"Retrying in ${delay.toMillis} ms ...",
        )

  private def decorateLogger(
    logger: Option[RollbarLogger],
    conf: RetryConfiguration,
  ): RollbarLogger = {
    val withMa = logger
      .getOrElse(log)
      .withKeyValue("max_attempts", conf.maxAttempts)
      .withSendToRollbar(false)
    val withTo = conf.timeout.fold(withMa)(t => withMa.withKeyValue("timeout_ms", t.toMillis))
    val withOt = conf.overallTimeout.fold(withTo)(ot => withTo.withKeyValue("overall_timeout_ms", ot.toMillis))
    withOt
  }

  private def shouldLogAttempt(attempt: Int): Boolean =
    attempt < 10 || attempt % 10 == 0

}

/** Configuration for retrying a Future on specified failures.
  * @param delayPolicy
  *   function to calculate the delay between attempts. The first attempt will be called with 1, the second with 2, etc.
  * @param maxAttempts
  *   maximum number of attempts to retry the Future. Default is 120.
  * @param timeout
  *   timeout after which to retry if the Future has not completed
  * @param overallTimeout
  *   overall timeout after which to stop retrying
  * @param forExceptions
  *   Partial function to match exceptions that should be retried. By default, no exceptions are retried. Use
  *   `forException` to add exceptions to the partial function.
  *
  * @example
  *   {{{
  *   val retryConfig =
  *     RetryConfiguration
  *        .withExponentialBackoff(1.seconds, 2)
  *        .withMaxAttempts(10)
  *        .withTimeout(Some(5.seconds))
  *        .withOverallTimeout(Some(2.minutes))
  *        .onExceptions[MyException]
  *        .onExceptionsMatching[IllegalArgumentException](_.getMessage.contains("foo"))
  *        .onExceptions {
  *          case e @ UnitResponse(status) if Set(429, 430).contains(status) => ()
  *          case e: ArithmeticException if e.getMessage.contains("baz") => ()
  *        }
  *   }}}
  */
case class RetryConfiguration(
  delayPolicy: DelayPolicy,
  maxAttempts: Int = 120,
  timeout: Option[FiniteDuration] = None,
  overallTimeout: Option[FiniteDuration] = None,
  forExceptions: PartialFunction[Throwable, Unit] = PartialFunction.empty,
) {
  def withMaxAttempts(maxAttempts: Int): RetryConfiguration = this.copy(maxAttempts = maxAttempts)
  def withDelayFunction(delayFunction: DelayPolicy): RetryConfiguration = this.copy(delayPolicy = delayFunction)
  def withTimeout(timeout: Option[FiniteDuration]): RetryConfiguration = this.copy(timeout = timeout)
  def withOverallTimeout(overallTimeout: Option[FiniteDuration]): RetryConfiguration =
    this.copy(overallTimeout = overallTimeout)

  def onExceptionsMatching[T <: Throwable](matches: T => Boolean)(implicit ct: ClassTag[T]): RetryConfiguration =
    this.copy(forExceptions = this.forExceptions.orElse { case e: T if matches(e) => () })
  def onExceptions[T <: Throwable](implicit ct: ClassTag[T]): RetryConfiguration =
    this.copy(forExceptions = this.forExceptions.orElse { case _: T => () })
  def onExceptions(pf: PartialFunction[Throwable, Unit]): RetryConfiguration =
    this.copy(forExceptions = this.forExceptions.orElse(pf))

  def withFixedDelay(delay: FiniteDuration): RetryConfiguration =
    this.copy(delayPolicy = FixedDelayPolicy(delay))

  def withExponentialBackoff(baseDelay: FiniteDuration, factor: Long = 2L): RetryConfiguration =
    this.copy(delayPolicy = ExponentialBackoffPolicy(baseDelay, factor))
}

trait DelayPolicy extends Product with Serializable {
  def delay(attempt: Int): FiniteDuration
}
case class FixedDelayPolicy(delay: FiniteDuration) extends DelayPolicy {
  override def delay(attempt: Int): FiniteDuration = delay
}
case class ExponentialBackoffPolicy(baseDelay: FiniteDuration, factor: Long = 2L) extends DelayPolicy {
  override def delay(attempt: Int): FiniteDuration =
    if (attempt == 1) baseDelay
    else baseDelay * BigInt(factor).pow(attempt - 1).toLong
}

object RetryConfiguration {
  def withFixedDelay(
    delay: FiniteDuration,
    maxAttempts: Int = 120,
    timeout: Option[FiniteDuration] = None,
    overallTimeout: Option[FiniteDuration] = None,
    forExceptions: PartialFunction[Throwable, Unit] = PartialFunction.empty,
  ): RetryConfiguration =
    RetryConfiguration(FixedDelayPolicy(delay), maxAttempts, timeout, overallTimeout, forExceptions)

  def withExponentialBackoff(
    baseDelay: FiniteDuration,
    factorDelay: Long = 2L,
    maxAttempts: Int = 120,
    timeout: Option[FiniteDuration] = None,
    overallTimeout: Option[FiniteDuration] = None,
    forExceptions: PartialFunction[Throwable, Unit] = PartialFunction.empty,
  ): RetryConfiguration =
    RetryConfiguration(
      ExponentialBackoffPolicy(baseDelay, factorDelay),
      maxAttempts,
      timeout,
      overallTimeout,
      forExceptions,
    )
}
