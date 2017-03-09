package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
import java.util.UUID
import javax.inject.{Inject, Singleton}

/**
  * Creates a valid X-Flow-Auth header for talking directly to a
  * service. Bound config must have a JWT_SALT parameter.
  */
@Singleton
class AuthHeaders @Inject() (
  config: Config
) {

  val FlowRequestId = "X-Flow-Request-Id"
  private[this] lazy val jwtSalt = config.requiredString("JWT_SALT")

  def headers(auth: AuthData): Seq[(String, String)] = {
    Seq(
      AuthHeaders.Header -> auth.jwt(jwtSalt),
      FlowRequestId -> auth.requestId
    )
  }

}


object AuthHeaders {

  val Header = "X-Flow-Auth"

  /**
    * Helper to create a valid auth data for this user (no organization)
    *
    * @param requestId Will be created if not specified
    */
  def user(
    user: UserReference,
    requestId: String = generateRequestId(),
    session: Option[FlowSession] = None
  ): AuthData.IdentifiedAuth = {
    AuthData.IdentifiedAuth(
      requestId = requestId,
      session = session,
      user = user
    )
  }

  /**
    * Helper to create a valid auth data for this user and organization.
    *
    * @param requestId Will be created if not specified
    */
  def organization(
    user: UserReference,
    org: String,
    role: Role = Role.Member,
    environment: Environment = Environment.Sandbox,
    requestId: String = generateRequestId(),
    session: Option[FlowSession] = None
): AuthData.IdentifiedOrgAuth = {
    AuthData.IdentifiedOrgAuth(
      requestId = requestId,
      session = session,
      user = user,
      orgData = OrgData.Identified(
        organization = org,
        role = role,
        environment = environment
      )
    )
  }

  private[this] def generateRequestId(): String = {
    generateRequestId("libplay")
  }

  /**
    * Generates a unique request id with the specified prefix
    * 
    * @param prefix e.g. libplay, api, console, etc. This is a string that
    *        will be prepended to the request id to help identify who
    *        created the request Id in the first place (useful for
    *        debugging). Recommend only using letters and numbers and
    *        no punctuation to make cut & paste easier.
    */
  def generateRequestId(prefix: String): String = {
    prefix + UUID.randomUUID.toString.replaceAll("-", "")
  }
}
