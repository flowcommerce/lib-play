package io.flow.play.clients

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
    id: _root_.scala.Option[Seq[String]] = None,
    token: _root_.scala.Option[String] = None,
    organizationId: _root_.scala.Option[String] = None,
    partnerId: _root_.scala.Option[String] = None,
    limit: Long = 25,
    offset: Long = 0,
    sort: String = "-created_at",
    requestHeaders: Seq[(String, String)] = Nil
  )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.token.v0.models.Token]] = scala.concurrent.Future {
    token match {
      case None => Nil
      case Some(t) => {
        data.tokens.filter { _.key == token }.map(_.token)
      }
    }
  }

  override def getById(
    id: String,
    requestHeaders: Seq[(String, String)] = Nil
  )(implicit ec: ExecutionContext): scala.concurrent.Future[Token] = get(id = Some(Seq(id))).map { t =>
    t.headOption.get
  }

}
