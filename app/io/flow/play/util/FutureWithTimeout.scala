package io.flow.play.util

import akka.actor.ActorSystem
import akka.pattern.after

import scala.concurrent._
import scala.concurrent.duration.FiniteDuration

object FutureWithTimeout {

  implicit class FutureExtensions[T](f: Future[T]) {
    def withTimeout(timeout: => Throwable)
                   (implicit duration: FiniteDuration,
                    system: ActorSystem,
                    ec: ExecutionContext): Future[T] = {
      Future firstCompletedOf Seq(
        f,
        after(duration, system.scheduler)(Future.failed(timeout))
      )
    }
  }

}
