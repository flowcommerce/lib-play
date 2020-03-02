/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.8.80
 * apibuilder 0.14.96 app.apibuilder.io/flow/error/latest/play_2_8_mock_client
 */
package io.flow.error.v0.mock {

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }

    def makeGenericErrorCode(): io.flow.error.v0.models.GenericErrorCode = io.flow.error.v0.models.GenericErrorCode.GenericError

    def makeGenericError(): io.flow.error.v0.models.GenericError = io.flow.error.v0.models.GenericError(
      code = io.flow.error.v0.mock.Factories.makeGenericErrorCode(),
      messages = Nil
    )

  }

}