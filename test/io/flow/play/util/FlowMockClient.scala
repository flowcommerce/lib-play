package io.flow.play.util

import java.util.concurrent.TimeUnit

import io.flow.common.v0.models.UserReference
import io.flow.play.util.FutureWithTimeout._

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait FlowMockClient[Client, GenericResponse, UnitResponse] extends FlowPlaySpec {

  val DefaultDuration = Duration(5, TimeUnit.SECONDS)
  def timeoutable[A](f: Future[A])
                    (implicit duration: FiniteDuration,
                     ec: ExecutionContext): Future[A] = f withTimeout new Exception(s"Timed out after $duration")
  override lazy val port = 9010
  private[this] val baseUrl = s"http://localhost:$port"

  def createAnonymousClient(baseUrl: String): Client

  def createIdentifiedClient(baseUrl: String,
                             user: UserReference = testUser,
                             org: Option[String] = None,
                             session: Option[FlowSession] = None): Client

  lazy val anonClient: Client = createAnonymousClient(baseUrl)

  def identifiedClient(
                        user: UserReference = testUser,
                        org: Option[String] = None,
                        session: Option[FlowSession] = None
                      ): Client = createIdentifiedClient(baseUrl, user, org, session)

  def expectErrors[T](f: => Future[T])
                     (implicit duration: FiniteDuration = DefaultDuration,
                      ec: ExecutionContext,
                      m: Manifest[GenericResponse]): GenericResponse = {
    Try(
      Await.result(timeoutable(f), duration)
    ) match {
      case Success(response) =>
        sys.error("Expected function to fail but it succeeded with: " + response)
      case Failure(ex) =>  ex match {
        case e: GenericResponse => e
        case e => sys.error(s"Expected an exception of type[GenericErrorResponse] but got[$e]")
      }
    }
  }

  def expectNotFound[T](f: => Future[T])
                       (implicit duration: FiniteDuration = DefaultDuration,
                        ec: ExecutionContext,
                        m: Manifest[UnitResponse]): Unit =
    expectStatus(404) {
      Await.result(timeoutable(f), duration)
    }

  def expectNotAuthorized[T](f: => Future[T])
                            (implicit duration: FiniteDuration = DefaultDuration,
                             ec: ExecutionContext,
                             m: Manifest[UnitResponse]): Unit =
    expectStatus(401) {
      Await.result(timeoutable(f), duration)
    }

  def expectStatus(code: Int)(f: => Unit)(implicit m: Manifest[UnitResponse]) {
    assert(code >= 400, s"code[$code] must be >= 400")

    Try(f) match {
      case Success(_) => org.specs2.execute.Failure(s"Expected HTTP[$code] but got HTTP 2xx")
      case Failure(ex) => ex match {
        case _: UnitResponse => org.specs2.execute.Success()
        case e => org.specs2.execute.Failure(s"Unexpected error: $e")
      }
    }
  }
}

