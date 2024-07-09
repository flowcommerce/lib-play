/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.22.86
 * User agent: apibuilder app.apibuilder.io/flow/token-internal/latest/play_2_8_mock_client
 */
package io.flow.token.internal.v0.mock {

  trait Client extends io.flow.token.internal.v0.interfaces.Client {

    val baseUrl: String = "http://mock.localhost"

    override def channelTokens: io.flow.token.internal.v0.ChannelTokens = MockChannelTokensImpl

  }

  object MockChannelTokensImpl extends MockChannelTokens

  trait MockChannelTokens extends io.flow.token.internal.v0.ChannelTokens {

    /**
     * Creates a channel token
     */
    def post(
      channelTokenForm: io.flow.token.v0.models.ChannelTokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.ChannelToken] = scala.concurrent.Future.successful {
      io.flow.token.v0.mock.Factories.makeChannelToken()
    }

  }

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }



  }

}