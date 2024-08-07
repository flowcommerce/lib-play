/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.18.69
 * User agent: apibuilder app.apibuilder.io/flow/token/latest/play_2_8_mock_client
 */
package io.flow.token.v0.mock {

  trait Client extends io.flow.token.v0.interfaces.Client {

    val baseUrl: String = "http://mock.localhost"

    override def channelTokens: io.flow.token.v0.ChannelTokens = MockChannelTokensImpl
    override def organizationTokens: io.flow.token.v0.OrganizationTokens = MockOrganizationTokensImpl
    override def organizationTokenV2: io.flow.token.v0.OrganizationTokenV2 = MockOrganizationTokenV2Impl
    override def partnerTokens: io.flow.token.v0.PartnerTokens = MockPartnerTokensImpl
    override def tokens: io.flow.token.v0.Tokens = MockTokensImpl
    override def tokenValidations: io.flow.token.v0.TokenValidations = MockTokenValidationsImpl

  }

  object MockChannelTokensImpl extends MockChannelTokens

  trait MockChannelTokens extends io.flow.token.v0.ChannelTokens {

    def get(
      channelId: String,
      id: _root_.scala.Option[Seq[String]] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.ChannelToken]] = scala.concurrent.Future.successful {
      Nil
    }

    def getById(
      channelId: String,
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.ChannelToken] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeChannelToken()
    }

    def putById(
      channelId: String,
      id: String,
      channelTokenForm: io.flow.token.v0.models.ChannelTokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.ChannelToken] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeChannelToken()
    }

    def deleteById(
      channelId: String,
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future.successful {
      // unit type
    }

  }

  object MockOrganizationTokensImpl extends MockOrganizationTokens

  trait MockOrganizationTokens extends io.flow.token.v0.OrganizationTokens {

    /**
     * Get all tokens for the specified organization
     *
     * @param mine Filter to tokens created by the requesting user
     */
    def get(
      organization: String,
      id: _root_.scala.Option[Seq[String]] = None,
      mine: _root_.scala.Option[Boolean] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.OrganizationToken]] = scala.concurrent.Future.successful {
      Nil
    }

    /**
     * Create a new organization
     */
    def post(
      organization: String,
      organizationTokenForm: io.flow.token.v0.models.OrganizationTokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.OrganizationToken] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeOrganizationToken()
    }

  }

  object MockOrganizationTokenV2Impl extends MockOrganizationTokenV2

  trait MockOrganizationTokenV2 extends io.flow.token.v0.OrganizationTokenV2 {

    /**
     * Creates an organization token
     */
    def post(
      organizationTokenFormV2: io.flow.token.v0.models.OrganizationTokenFormV2,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.OrganizationTokenV2] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeOrganizationTokenV2()
    }

  }

  object MockPartnerTokensImpl extends MockPartnerTokens

  trait MockPartnerTokens extends io.flow.token.v0.PartnerTokens {

    /**
     * Get all tokens for the specified partner
     *
     * @param mine Filter to tokens created by the requesting user
     */
    def get(
      partner: String,
      id: _root_.scala.Option[Seq[String]] = None,
      mine: _root_.scala.Option[Boolean] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.PartnerToken]] = scala.concurrent.Future.successful {
      Nil
    }

    /**
     * Create a new partner
     */
    def post(
      partner: String,
      partnerTokenForm: io.flow.token.v0.models.PartnerTokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.PartnerToken] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makePartnerToken()
    }

  }

  object MockTokensImpl extends MockTokens

  trait MockTokens extends io.flow.token.v0.Tokens {

    /**
     * Get all tokens that you are authorized to view. Note that the cleartext token
     * value is never sent. To view the API token itself, see the resource path
     * /tokens/:id/cleartext
     *
     * @param organization Filter to tokens created for this organization
     * @param partner Filter to tokens created for this partner
     * @param mine Filter to tokens created by the requesting user
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      organization: _root_.scala.Option[String] = None,
      partner: _root_.scala.Option[String] = None,
      mine: _root_.scala.Option[Boolean] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future.successful {
      Nil
    }

    /**
     * Get metadata for the token with this ID
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    /**
     * Retrieves the token with the actual string token in cleartext
     */
    def getCleartextById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Cleartext] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeCleartext()
    }

    /**
     * Old method to validate a token, obtaining specific information if the token is
     * valid (or a 404 if the token does not exist). We use an HTTP POST with a form
     * body to ensure that the token itself is not logged in the request logs.
     */
    @deprecated("Use /authentications/v2, as it also does RBAC validation")
    def postAuthentications(
      tokenAuthenticationForm: io.flow.token.v0.models.TokenAuthenticationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.TokenReference] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeTokenReference()
    }

    /**
     * Preferred method to validate a token (including RBAC checks), obtaining specific
     * information if the token is valid (or a 404 if the token does not exist). We use
     * an HTTP POST with a form body to ensure that the token itself is not logged in
     * the request logs.
     */
    def postAuthenticationsAndV2(
      tokenRbacAuthenticationForm: io.flow.token.v0.models.TokenRbacAuthenticationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.TokenReference] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeTokenReference()
    }

    def deleteById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future.successful {
      // unit type
    }

  }

  object MockTokenValidationsImpl extends MockTokenValidations

  trait MockTokenValidations extends io.flow.token.v0.TokenValidations {

    def post(
      tokenValidationForm: io.flow.token.v0.models.TokenValidationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.TokenValidation] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeTokenValidation()
    }

  }

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }

    def makeChannelToken(): io.flow.token.v0.models.ChannelToken = io.flow.token.v0.models.ChannelToken(
      id = Factories.randomString(24),
      channel = io.flow.common.v0.mock.Factories.makeChannelReference(),
      user = io.flow.common.v0.mock.Factories.makeUserReference(),
      partial = Factories.randomString(24),
      cleartext = None,
      createdAt = _root_.org.joda.time.DateTime.now,
      description = None
    )

    def makeChannelTokenForm(): io.flow.token.v0.models.ChannelTokenForm = io.flow.token.v0.models.ChannelTokenForm(
      channelId = Factories.randomString(24),
      description = None
    )

    def makeChannelTokenReference(): io.flow.token.v0.models.ChannelTokenReference = io.flow.token.v0.models.ChannelTokenReference(
      id = Factories.randomString(24),
      channel = io.flow.common.v0.mock.Factories.makeChannelReference(),
      user = io.flow.common.v0.mock.Factories.makeUserReference()
    )

    def makeCleartext(): io.flow.token.v0.models.Cleartext = io.flow.token.v0.models.Cleartext(
      value = Factories.randomString(24)
    )

    def makeOrganizationToken(): io.flow.token.v0.models.OrganizationToken = io.flow.token.v0.models.OrganizationToken(
      id = Factories.randomString(24),
      organization = io.flow.common.v0.mock.Factories.makeOrganizationReference(),
      user = io.flow.common.v0.mock.Factories.makeUserReference(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      partial = Factories.randomString(24),
      createdAt = _root_.org.joda.time.DateTime.now,
      description = None
    )

    def makeOrganizationTokenForm(): io.flow.token.v0.models.OrganizationTokenForm = io.flow.token.v0.models.OrganizationTokenForm(
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      description = None
    )

    def makeOrganizationTokenFormV2(): io.flow.token.v0.models.OrganizationTokenFormV2 = io.flow.token.v0.models.OrganizationTokenFormV2(
      organizationId = Factories.randomString(24),
      description = None
    )

    def makeOrganizationTokenReference(): io.flow.token.v0.models.OrganizationTokenReference = io.flow.token.v0.models.OrganizationTokenReference(
      id = Factories.randomString(24),
      organization = io.flow.common.v0.mock.Factories.makeOrganizationReference(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      user = io.flow.common.v0.mock.Factories.makeUserReference()
    )

    def makeOrganizationTokenV2(): io.flow.token.v0.models.OrganizationTokenV2 = io.flow.token.v0.models.OrganizationTokenV2(
      id = Factories.randomString(24),
      organization = io.flow.common.v0.mock.Factories.makeOrganizationReference(),
      user = io.flow.common.v0.mock.Factories.makeUserReference(),
      partial = Factories.randomString(24),
      cleartext = None,
      createdAt = _root_.org.joda.time.DateTime.now,
      description = None
    )

    def makeOrganizationTokenV2Reference(): io.flow.token.v0.models.OrganizationTokenV2Reference = io.flow.token.v0.models.OrganizationTokenV2Reference(
      id = Factories.randomString(24),
      organization = io.flow.common.v0.mock.Factories.makeOrganizationReference()
    )

    def makePartnerToken(): io.flow.token.v0.models.PartnerToken = io.flow.token.v0.models.PartnerToken(
      id = Factories.randomString(24),
      partner = io.flow.token.v0.mock.Factories.makeTokenPartnerReference(),
      user = io.flow.common.v0.mock.Factories.makeUserReference(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      partial = Factories.randomString(24),
      createdAt = _root_.org.joda.time.DateTime.now,
      description = None
    )

    def makePartnerTokenForm(): io.flow.token.v0.models.PartnerTokenForm = io.flow.token.v0.models.PartnerTokenForm(
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      description = None
    )

    def makePartnerTokenReference(): io.flow.token.v0.models.PartnerTokenReference = io.flow.token.v0.models.PartnerTokenReference(
      id = Factories.randomString(24),
      partner = io.flow.token.v0.mock.Factories.makeTokenPartnerReference(),
      environment = io.flow.common.v0.mock.Factories.makeEnvironment(),
      user = io.flow.common.v0.mock.Factories.makeUserReference()
    )

    def makeTokenAuthenticationForm(): io.flow.token.v0.models.TokenAuthenticationForm = io.flow.token.v0.models.TokenAuthenticationForm(
      token = Factories.randomString(24)
    )

    def makeTokenPartnerReference(): io.flow.token.v0.models.TokenPartnerReference = io.flow.token.v0.models.TokenPartnerReference(
      id = Factories.randomString(24)
    )

    def makeTokenRbacAuthenticationForm(): io.flow.token.v0.models.TokenRbacAuthenticationForm = io.flow.token.v0.models.TokenRbacAuthenticationForm(
      token = Factories.randomString(24),
      method = Factories.randomString(24),
      pathPattern = Factories.randomString(24),
      path = Factories.randomString(24)
    )

    def makeTokenValidation(): io.flow.token.v0.models.TokenValidation = io.flow.token.v0.models.TokenValidation(
      status = Factories.randomString(24)
    )

    def makeTokenValidationForm(): io.flow.token.v0.models.TokenValidationForm = io.flow.token.v0.models.TokenValidationForm(
      token = Factories.randomString(24)
    )

    def makeToken(): io.flow.token.v0.models.Token = io.flow.token.v0.mock.Factories.makeChannelToken()

    def makeTokenReference(): io.flow.token.v0.models.TokenReference = io.flow.token.v0.mock.Factories.makeChannelTokenReference()

  }

}