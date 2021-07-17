package io.flow.play.util

import play.api.mvc.{Result => PlayResult}
import scala.concurrent.{ExecutionContext, Future}

trait Result[A] extends AsyncResult[A] {
  def toPlayResult(value: A): PlayResult
  def toPlayAsyncResult(value: A): Future[PlayResult] =
    Future.successful(toPlayResult(value))
}

object Result {
  def apply[A: Result] = implicitly[Result[A]]
  implicit class ResultOps[A: Result](a: A) {
    def toPlayResult = Result[A].toPlayResult(a)
  }

  implicit def toPlayResult[A: Result](value: A): PlayResult =
    value.toPlayResult

  implicit val resultToR = new Result[PlayResult] {
    def toPlayResult(value: PlayResult): PlayResult = value
  }

  implicit def eitherRToR[A: Result, B: Result] = new Result[Either[A, B]] {
    def toPlayResult(value: Either[A, B]): PlayResult =
      value.fold(Result[A].toPlayResult, Result[B].toPlayResult)
  }
}

trait AsyncResult[A] {
  def toPlayAsyncResult(value: A): Future[PlayResult]
}

object AsyncResult {
  def apply[A: AsyncResult] = implicitly[AsyncResult[A]]
  implicit class ResultOps[A: AsyncResult](a: A) {
    def toPlayAsyncResult = AsyncResult[A].toPlayAsyncResult(a)
  }

  implicit def toPlayAsyncResult[A: AsyncResult](value: A): Future[PlayResult] =
    value.toPlayAsyncResult

  implicit def eitherRToAR[A: AsyncResult, B: AsyncResult] =
    new AsyncResult[Either[A, B]] {
      def toPlayAsyncResult(value: Either[A, B]): Future[PlayResult] =
        value.fold(AsyncResult[A].toPlayAsyncResult,
                   AsyncResult[B].toPlayAsyncResult)
    }

  implicit def futureRToAR[A: AsyncResult](implicit ec: ExecutionContext) =
    new AsyncResult[Future[A]] {
      def toPlayAsyncResult(value: Future[A]): Future[PlayResult] =
        value.flatMap(AsyncResult[A].toPlayAsyncResult)
    }

}
