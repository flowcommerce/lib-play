/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.36
 * apidoc:0.11.27 http://www.apidoc.me/flow/token/0.0.36/play_2_4_mock_client
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
     * Get user reference by token
     */
    def get(
      token: Seq[String],
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
      Nil
    }

    /**
     * Get the user for this specified token
     */
    def getByToken(
      token: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    /**
     * Create a user token
     */
    def post(
      tokenForm: io.flow.token.v0.models.TokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    def deleteByToken(
      token: String,
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

    def makeToken() = io.flow.token.v0.models.Token(
      user = io.flow.common.v0.mock.Factories.makeUserReference()
    )

    def makeTokenForm() = io.flow.token.v0.models.TokenForm(
      userId = randomString(),
      description = None
    )

    def makeValidation() = io.flow.token.v0.models.Validation(
      status = randomString(),
      description = None
    )

    def makeValidationForm() = io.flow.token.v0.models.ValidationForm(
      token = randomString()
    )

  }

}