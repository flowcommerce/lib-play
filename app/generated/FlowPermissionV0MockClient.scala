/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.9.33
 * apibuilder 0.15.8 app.apibuilder.io/flow/permission/latest/play_2_8_mock_client
 */
package io.flow.permission.v0.mock {

  trait Client extends io.flow.permission.v0.interfaces.Client {

    val baseUrl: String = "http://mock.localhost"

    override def flowRoles: io.flow.permission.v0.FlowRoles = MockFlowRolesImpl
    override def permissionChecks: io.flow.permission.v0.PermissionChecks = MockPermissionChecksImpl

  }

  object MockFlowRolesImpl extends MockFlowRoles

  trait MockFlowRoles extends io.flow.permission.v0.FlowRoles {

    /**
     * List roles in use by this organization.
     */
    def getOrganizationAndRolesByOrganization(
      organization: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.permission.v0.models.FlowRole]] = scala.concurrent.Future.successful {
      Nil
    }

  }

  object MockPermissionChecksImpl extends MockPermissionChecks

  trait MockPermissionChecks extends io.flow.permission.v0.PermissionChecks {

    /**
     * Returns permissions for the specified organization for the current request. Used
     * by integrators to test headers.
     *
     * @param permittedMethod If provided, only routes matching this method will be returned.
     * @param path If provided, only routes matching theis regular expression will be retunerd.
     */
    def getPermissionAndChecksAndAllByOrganization(
      organization: String,
      permittedMethod: _root_.scala.Option[io.flow.permission.v0.models.PermittedHttpMethod] = None,
      path: _root_.scala.Option[String] = None,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.permission.v0.models.PermissionCheck] = scala.concurrent.Future.successful {
      io.flow.permission.v0.mock.Factories.makePermissionCheck()
    }

  }

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }

    def makeAuthenticationTechnique(): io.flow.permission.v0.models.AuthenticationTechnique = io.flow.permission.v0.models.AuthenticationTechnique.Anonymous

    def makeFlowBehavior(): io.flow.permission.v0.models.FlowBehavior = io.flow.permission.v0.models.FlowBehavior.ViewConsumerData

    def makeFlowRole(): io.flow.permission.v0.models.FlowRole = io.flow.permission.v0.models.FlowRole.OrganizationAdmin

    def makePermittedHttpMethod(): io.flow.permission.v0.models.PermittedHttpMethod = io.flow.permission.v0.models.PermittedHttpMethod.Get

    def makeBehaviorAudit(): io.flow.permission.v0.models.BehaviorAudit = io.flow.permission.v0.models.BehaviorAudit(
      behavior = io.flow.permission.v0.mock.Factories.makeFlowBehavior(),
      authenticationTechniques = Nil,
      roles = Nil
    )

    def makePermissionAudit(): io.flow.permission.v0.models.PermissionAudit = io.flow.permission.v0.models.PermissionAudit(
      routes = Nil,
      behaviors = Nil
    )

    def makePermissionCheck(): io.flow.permission.v0.models.PermissionCheck = io.flow.permission.v0.models.PermissionCheck(
      authenticationTechnique = io.flow.permission.v0.mock.Factories.makeAuthenticationTechnique(),
      user = None,
      roles = Nil,
      behaviors = Nil,
      routes = Nil
    )

    def makePermissionError(): io.flow.permission.v0.models.PermissionError = io.flow.permission.v0.models.PermissionError(
      code = io.flow.error.v0.mock.Factories.makeGenericErrorCode(),
      messages = Nil,
      grantingRoles = None,
      adminUsers = None
    )

    def makePermittedRoute(): io.flow.permission.v0.models.PermittedRoute = io.flow.permission.v0.models.PermittedRoute(
      method = io.flow.permission.v0.mock.Factories.makePermittedHttpMethod(),
      path = Factories.randomString(24)
    )

    def makeRouteAudit(): io.flow.permission.v0.models.RouteAudit = io.flow.permission.v0.models.RouteAudit(
      method = io.flow.permission.v0.mock.Factories.makePermittedHttpMethod(),
      path = Factories.randomString(24),
      authenticationTechniques = Nil,
      roles = Nil
    )

    def makeSimplePermissionCheck(): io.flow.permission.v0.models.SimplePermissionCheck = io.flow.permission.v0.models.SimplePermissionCheck(
      behaviors = Nil
    )

  }

}