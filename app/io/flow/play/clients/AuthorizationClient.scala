package io.flow.play.clients

import io.flow.play.util.DefaultConfig

import io.flow.authorization.v0.{Authorization, Client}
import io.flow.authorization.v0.models.{Check, Privilege}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

trait AuthorizationClient {

  /**
    * Authorizes this user - will return true if the provide user has
    * each privilege specified for all contexts
    * 
    *  @param creates if specified, this is the context for which the user must have been granted the Create privilege.
    *  @param reads if specified, this is the context for which the user must have been granted the Read privilege.
    *  @param updates if specified, this is the context for which the user must have been granted the Update privilege.
    *  @param deletes if specified, this is the context for which the user must have been granted the Delete privilege.
    */
  def authorize(
    userId: String,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check]

}

@javax.inject.Singleton
class DefaultAuthorizationClient() extends AuthorizationClient {

  def host: String = DefaultConfig.requiredString("authorization.api.host")

  lazy val client = new Client(host)

  private[this] val DefaultDenial = Check(
    result = false,
    reason = "No contexts were provided - by default we deny access"
  )

  override def authorize(
    userId: String,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check] = {
    assert(
      Seq(creates, reads, updates, deletes).flatten.size <= 1,
      "TODO: We have not yet implemented multiple context permission checks"
    )

    if (!creates.isEmpty) {
      checkAuthorization(userId, Privilege.Create, creates.get)

    } else if (!reads.isEmpty) {
      checkAuthorization(userId, Privilege.Read, reads.get)

    } else if (!updates.isEmpty) {
      checkAuthorization(userId, Privilege.Update, updates.get)

    } else if (!deletes.isEmpty) {
      checkAuthorization(userId, Privilege.Delete, updates.get)

    } else {
      Future {
        DefaultDenial
      }
    }
  }

  private def checkAuthorization(
    userId: String,
    privilege: Privilege,
    context: String
  )(implicit ec: ExecutionContext): Future[Check] = {
    client.checks.get(
      userId = userId,
      privilege = privilege,
      context = context
    )
  }

}
