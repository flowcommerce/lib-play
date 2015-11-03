package io.flow.play.clients

import io.flow.play.util.Config

import io.flow.authorization.v0.{Authorization, Client}
import io.flow.authorization.v0.models.{Check, Privilege}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import java.util.UUID

trait AuthorizationsClient {

  /**
    * Authorizes this user - will return true if the provide user has
    * each privilege specified for all contexts
    * 
    *  @creates if specified, this is the context for which the user must have been granted the Create privilege.
    *  @reads if specified, this is the context for which the user must have been granted the Read privilege.
    *  @updates if specified, this is the context for which the user must have been granted the Update privilege.
    *  @deletes if specified, this is the context for which the user must have been granted the Delete privilege.
    */
  def authorize(
    userGuid: UUID,
    creates: Option[String] = None,
    reads: Option[String] = None,
    updates: Option[String] = None,
    deletes: Option[String] = None
  )(implicit ec: ExecutionContext): Future[Check]

}

case class PlayAuthorizationsClient(
  host: String = Config.requiredString("authorization.api.host"),
  token: String = Config.requiredString("authorization.api.token")
) extends AuthorizationsClient {

  val client = new Client(
    apiUrl = host,
    auth = Some(
      Authorization.Basic(
        username = token,
        password = None
      )
    )
  )

  private[this] val DefaultDenial = Check(
    result = false,
    reason = "No contexts were provided - by default we deny access"
  )

  override def authorize(
    userGuid: UUID,
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
      checkAuthorization(userGuid, Privilege.Create, creates.get)

    } else if (!reads.isEmpty) {
      checkAuthorization(userGuid, Privilege.Read, reads.get)

    } else if (!updates.isEmpty) {
      checkAuthorization(userGuid, Privilege.Update, updates.get)

    } else if (!deletes.isEmpty) {
      checkAuthorization(userGuid, Privilege.Delete, updates.get)

    } else {
      Future {
        DefaultDenial
      }
    }
  }

  private def checkAuthorization(
    userGuid: UUID,
    privilege: Privilege,
    context: String
  )(implicit ec: ExecutionContext): Future[Check] = {
    client.checks.get(
      userGuid = userGuid,
      privilege = privilege,
      context = context
    )
  }

}
