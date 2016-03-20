package io.flow.play.clients

import io.flow.common.v0.models.{User, UserReference}
import io.flow.token.v0.mock
import io.flow.token.v0.models.Token
import scala.concurrent.{ExecutionContext, Future}

/**
  * This object emulates storage of the tokens for the mock token
  * client
  */
case class MockTokenData() {

  case class InternalToken(key: String, token: Token)

  var tokens = scala.collection.mutable.ListBuffer[InternalToken]()

  /**
    * @param key: The actual token
    */
  def add(key: String, token: Token) = {
    tokens += InternalToken(key, token)
  }

}

@javax.inject.Singleton
class MockTokenClient @javax.inject.Inject() () extends mock.Client {

  val data = MockTokenData()

  override def tokens = MockTokens(data)

}

case class MockTokens(data: MockTokenData) extends mock.MockTokens {

  override def get(
    token: Seq[String],
    requestHeaders: Seq[(String, String)] = Nil
  )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
    data.tokens.filter { t =>
      token.contains(t.key)
    }.map(_.token)
  }

  override def getByToken(
    token: String,
    requestHeaders: Seq[(String, String)] = Nil
  )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.token.v0.models.Token] = {
    get(Seq(token)).map { _.headOption.getOrElse {
      throw new io.flow.token.v0.errors.UnitResponse(404)
    }}
  }

}


