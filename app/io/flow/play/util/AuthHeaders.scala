package io.flow.play.util

import io.flow.common.v0.models.{Environment, Role, UserReference}
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

  private[this] lazy val jwtSalt = Salts(config).preferred

  def headers(auth: AuthData): Seq[(String, String)] = {
    Seq(
      AuthHeaders.Header -> auth.jwt(jwtSalt),
      FlowRequestId -> auth.requestId
    )
  }

}


object AuthHeaders {

  private[this] val random = Random()

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
  ): AuthData.Identified = {
    AuthData.Identified(
      requestId = requestId,
      user = user,
      session = session
    )
  }

  def session(
    requestId: String = generateRequestId(),
    session: FlowSession = createFlowSession()
  ): AuthData.Session = {
    AuthData.Session(
      requestId = requestId,
      session = session
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
): OrgAuthData.Identified = {
    OrgAuthData.Identified(
      requestId = requestId,
      user = user,
      organization = org,
      environment = environment,
      role = role,
      session = session
    )
  }

  /**
    * Helper to create a valid session auth data
    *
    * @param requestId Will be created if not specified
    */
  def organizationSession(
    org: String,
    environment: Environment = Environment.Sandbox,
    requestId: String = generateRequestId(),
    session: FlowSession = createFlowSession()
  ): OrgAuthData.Session = {
    OrgAuthData.Session(
      requestId = requestId,
      session = session,
      organization = org,
      environment = environment
    )
  }

  def createFlowSession(): FlowSession = {
    FlowSession(
      id = Constants.Prefixes.Session + random.alphaNumeric(36)
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
    prefix + random.alphaNumeric(36)
  }

}
