package io.flow.play.clients

import io.flow.token.v0.mock
import io.flow.token.v0.models.TokenReference

import scala.concurrent.{ExecutionContext, Future}

/**
  * This object emulates storage of the tokens for the mock token
  * client
  */
case class MockTokenData() {

  case class InternalToken(key: String, token: TokenReference)

  var tokens = scala.collection.mutable.ListBuffer[InternalToken]()

  /**
    * @param key: The actual token
    */
  def add(key: String, token: TokenReference) = {
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
  )(implicit ec: ExecutionContext): scala.concurrent.Future[Seq[TokenReference]] = Future {
    data.tokens.filter { t =>
      token.contains(t.key)
    }.map(_.token)
  }

  override def getByToken(
    token: String,
    requestHeaders: Seq[(String, String)] = Nil
  )(implicit ec: ExecutionContext): scala.concurrent.Future[TokenReference] = get(Seq(token)).map { t =>
    t.headOption.get
  }

}


