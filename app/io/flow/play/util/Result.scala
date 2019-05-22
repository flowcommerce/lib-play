package io.flow.play.util

import play.api.mvc.{Result => PlayResult}
import scala.concurrent.{ExecutionContext, Future}

trait Result[A] {
  def toPlayResult(value: A): Future[PlayResult]
}

object Result {
  def apply[A: Result] = implicitly[Result[A]]
  implicit class ResultOps[A: Result](a: A) {
    def toPlayResult = Result[A].toPlayResult(a)
  }

  implicit def toPlayResult[A: Result](value: A): Future[PlayResult] =
    value.toPlayResult

  implicit val resultToR = new Result[PlayResult] {
    def toPlayResult(value: PlayResult) = Future.successful(value)
  }

  implicit def futureRToR[A: Result](implicit ec: ExecutionContext) =
    new Result[Future[A]] {
      def toPlayResult(value: Future[A]) = value.flatMap(_.toPlayResult)
    }

  implicit def eitherRToR[A: Result, B: Result] = new Result[Either[A, B]] {
    def toPlayResult(value: Either[A, B]) =
      value.fold(_.toPlayResult, _.toPlayResult)
  }
}
