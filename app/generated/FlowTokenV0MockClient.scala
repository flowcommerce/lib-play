/**
 * Generated by apidoc - http://www.apidoc.me
 * Service version: 0.0.1
 * apidoc:0.11.17 http://www.apidoc.me/flow/token/0.0.1/play_2_4_mock_client
 */
package io.flow.token.v0.mock {

  trait Client extends io.flow.token.v0.interfaces.Client {

    override def healthchecks: MockHealthchecks = MockHealthchecksImpl
    override def tokens: MockTokens = MockTokensImpl

  }

  object MockHealthchecksImpl extends MockHealthchecks

  trait MockHealthchecks extends io.flow.token.v0.Healthchecks {

    def getHealthcheck()(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.Healthcheck] = scala.concurrent.Future {
      io.flow.common.v0.mock.Factories.makeHealthcheck()
    }

  }

  object MockTokensImpl extends MockTokens

  trait MockTokens extends io.flow.token.v0.Tokens {

    /**
     * Get user reference by token
     */
    def get(
      token: Seq[String]
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
      Nil
    }

    /**
     * Get the user for this specified token
     */
    def getByToken(
      token: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = scala.concurrent.Future {
      io.flow.token.v0.mock.Factories.makeToken()
    }

    /**
     * Create a user token
     */
    def post(
      tokenForm: io.flow.token.v0.models.TokenForm
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
      Nil
    }

    def deleteByToken(
      token: String
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future {
      // unit type
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

  }

}