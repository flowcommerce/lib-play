package io.flow.play.clients

import io.flow.authorization.v0.models.{Authorization, Check}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class MockAuthorizationClient extends AuthorizationClient {

  def grantAll(userId: String) {
    MockAuthorizationClient.grantAll(userId)
  }

  def revokeAll(userId: String) {
    MockAuthorizationClient.revokeAll(userId)
  }

  override def authorize(
    userId: String,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check] = {
    MockAuthorizationClient.authorize(
      userId = userId,
      creates = creates,
      reads = reads,
      updates = updates,
      deletes = deletes
    )
  }

}


object MockAuthorizationClient {

  private val Granted = Check(
    result = true,
    reason = "User authorized"
  )

  private val Denied = Check(
    result = false,
    reason = "User not authorized"
  )

  private[this] val DeniedForNoContext = Check(
    result = false,
    reason = "No contexts were provided - by default we deny access"
  )

  private var grantAll = scala.collection.mutable.ListBuffer[String]()
  private var revokeAll = scala.collection.mutable.ListBuffer[String]()

  def grantAll(userId: String) {
    grantAll.append(userId)
  }

  def revokeAll(userId: String) {
    revokeAll.append(userId)
  }

  def authorize(
    userId: String,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check] = {
    Future {
      if (creates.isEmpty && reads.isEmpty && updates.isEmpty && deletes.isEmpty) {
        DeniedForNoContext
      } else {
        (grantAll.contains(userId), revokeAll.contains(userId)) match {
          case (true, false) => Granted
          case _ => Denied
        }
      }
    }
  }
}
