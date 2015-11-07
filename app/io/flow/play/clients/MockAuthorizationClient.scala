package io.flow.play.clients

import java.util.UUID
import io.flow.authorization.v0.models.{Authorization, Check}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@javax.inject.Singleton
class MockAuthorizationClient extends AuthorizationClient {

  def grantAll(userGuid: UUID) {
    MockAuthorizationClient.grantAll(userGuid)
  }

  def revokeAll(userGuid: UUID) {
    MockAuthorizationClient.revokeAll(userGuid)
  }

  override def authorize(
    userGuid: UUID,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check] = {
    MockAuthorizationClient.authorize(
      userGuid = userGuid,
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

  private var grantAll = scala.collection.mutable.ListBuffer[UUID]()
  private var revokeAll = scala.collection.mutable.ListBuffer[UUID]()

  def grantAll(userGuid: UUID) {
    grantAll.append(userGuid)
  }

  def revokeAll(userGuid: UUID) {
    revokeAll.append(userGuid)
  }

  def authorize(
    userGuid: UUID,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check] = {
    Future {
      if (creates.isEmpty && reads.isEmpty && updates.isEmpty && deletes.isEmpty) {
        DeniedForNoContext
      } else {
        (grantAll.contains(userGuid), revokeAll.contains(userGuid)) match {
          case (true, false) => Granted
          case _ => Denied
        }
      }
    }
  }
}
