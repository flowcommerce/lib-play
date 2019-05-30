package io.flow.play.controllers

import io.flow.common.v0.models.{CustomerReference, Environment, UserReference}
import io.flow.play.util._
import play.api.mvc._

class AnonymousRequest[A](
  val auth: AuthData.Anonymous,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: Option[UserReference] = auth.user
}

class SessionOrgRequest[A](
  val auth: OrgAuthData.Session,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val flowSession: FlowSession = auth.session
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

class IdentifiedRequest[A](
  val auth: AuthData.Identified,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
}

class SessionRequest[A](
  val auth: AuthData.Session,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val flowSession: FlowSession = auth.session
}

class CustomerRequest[A](
  val auth: AuthData.Customer,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val flowSession: FlowSession = auth.session
  val customer: CustomerReference = auth.customer
}

class IdentifiedOrgRequest[A](
  val auth: OrgAuthData.Identified,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val user: UserReference = auth.user
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

class CustomerOrgRequest[A](
  val auth: OrgAuthData.Customer,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val organization: String = auth.organization
  val environment: Environment = auth.environment
  val flowSession: FlowSession = auth.session
  val customer: CustomerReference = auth.customer
}

/**
  * Any type of request that contains org data
  */
class OrgRequest[A](
  val auth: OrgAuthData,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

/**
  * Any type of request that contains checkout data
  */
class CheckoutRequest[A](
  val auth: AuthData,
  request: Request[A]
) extends WrappedRequest[A](request)

/**
  * Any type of request that contains checkout org data
  */
class CheckoutOrgRequest[A](
  val auth: OrgAuthData,
  request: Request[A]
) extends WrappedRequest[A](request) {
  val organization: String = auth.organization
  val environment: Environment = auth.environment
}

