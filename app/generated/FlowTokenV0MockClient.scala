/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.45
 * apidoc:0.11.32 http://www.apidoc.me/flow/token/0.0.45/play_2_4_mock_client
 */
package io.flow.token.v0.mock {

  trait Client extends io.flow.token.v0.interfaces.Client {

    val baseUrl = "http://mock.localhost"

    override def tokens: MockTokens = MockTokensImpl
    override def validations: MockValidations = MockValidationsImpl

  }

  object MockTokensImpl extends MockTokens

  trait MockTokens extends io.flow.token.v0.Tokens {

    /**
     * Get all tokens that you are authorized to view. Note that the cleartext token
     * value is never sent.
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      token: _root_.scala.Option[String] = None,
      limit: Long = 25,
      offset: Long = 0,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
      Nil
    }

    /**
     * Get metadata for the token with this ID
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    /**
     * Preferred method to validate a token, obtaining user information if the token is
     * valid (or a 404 if the token does not exist). We use an HTTP POST with a form
     * body to enusre that the token itself is not logged in the request logs.
     */
    def postAuthentications(
      authenticationForm: io.flow.token.v0.models.AuthenticationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.TokenReference] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeTokenReference()
    }

    /**
     * Create a new token for the requesting user
     */
    def post(
      tokenForm: io.flow.token.v0.models.TokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    def deleteById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future {
      // unit type
    }

  }

  object MockValidationsImpl extends MockValidations

  trait MockValidations extends io.flow.token.v0.Validations {

    def post(
      validationForm: io.flow.token.v0.models.ValidationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Validation] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeValidation()
    }

  }

  object Factories {

    def randomString(): String = {
      "Test " + _root_.java.util.UUID.randomUUID.toString.replaceAll("-", " ")
    }

    def makeAuthenticationForm() = io.flow.token.v0.models.AuthenticationForm(
      token = randomString()
    )

    def makeToken() = io.flow.token.v0.models.Token(
      id = randomString(),
      user = io.flow.common.v0.mock.Factories.makeUserReference(),
      createdAt = new org.joda.time.DateTime(),
      description = None
    )

    def makeTokenForm() = io.flow.token.v0.models.TokenForm(
      description = None
    )

    def makeTokenReference() = io.flow.token.v0.models.TokenReference(
      id = randomString(),
      user = io.flow.common.v0.mock.Factories.makeUserReference()
    )

    def makeValidation() = io.flow.token.v0.models.Validation(
      status = randomString()
    )

    def makeValidationForm() = io.flow.token.v0.models.ValidationForm(
      token = randomString()
    )

  }

}