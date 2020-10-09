/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.9.63
 * apibuilder 0.15.11 app.apibuilder.io/flow/healthcheck/latest/play_2_8_mock_client
 */
package io.flow.healthcheck.v0.mock {

  trait Client extends io.flow.healthcheck.v0.interfaces.Client {

    val baseUrl: String = "http://mock.localhost"

    override def healthchecks: io.flow.healthcheck.v0.Healthchecks = MockHealthchecksImpl

  }

  object MockHealthchecksImpl extends MockHealthchecks

  trait MockHealthchecks extends io.flow.healthcheck.v0.Healthchecks {

    def getHealthcheck(
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.healthcheck.v0.models.Healthcheck] = scala.concurrent.Future.successful {
      io.flow.healthcheck.v0.mock.Factories.makeHealthcheck()
    }

  }

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }

    def makeHealthcheck(): io.flow.healthcheck.v0.models.Healthcheck = io.flow.healthcheck.v0.models.Healthcheck(
      status = Factories.randomString(24)
    )

  }

}