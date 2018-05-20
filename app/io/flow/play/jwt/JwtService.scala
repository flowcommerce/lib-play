package io.flow.play.jwt

import akka.actor.ActorSystem
import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[NotImplementedJwtService])
trait JwtService {

  def decode(jwt: String): Try[Map[String, String]]

  def encode(claims: Map[String, String]): String

}

@Singleton
class DefaultJwtService @Inject() (
  retrieverService: JwtSecretsRetrieverService,
  system: ActorSystem
)(implicit ec: ExecutionContext) extends JwtService {

  import DefaultJwtService._

  private[this] val header = JwtHeader("HS256")

  override def decode(jwt: String): Try[Map[String, String]] = jwt match {
    case JsonWebToken(_, claimsSet, _) =>
      val claimsOpt = for {
        // Swallow the json conversion errors
        claimsMap <- claimsSet.asSimpleMap.toOption
        usedSecret <- retrieverService.get.all.get(claimsMap.getOrElse(KeyId, FallbackKeyId))
        if JsonWebToken.validate(jwt, usedSecret.key)
      } yield claimsMap - KeyId
      claimsOpt.map(Success(_)).getOrElse(JwtFailure)

    case _ => JwtFailure
  }

  override def encode(claims: Map[String, String]): String = {
    val secret = retrieverService.get.encoding
    val claimsWithKeyId = claims + (KeyId -> secret.id)
    val claimsSet = JwtClaimsSet(claimsWithKeyId)
    JsonWebToken(header, claimsSet, secret.key)
  }

}

object DefaultJwtService {

  private val KeyId = "kid"
  // TODO: remove when obsolete
  private val FallbackKeyId = "sec-201610-1"
  private[jwt] val JwtFailure = Failure(new IllegalArgumentException("Not a valid JsonWebToken"))

}

@Singleton
class NotImplementedJwtService @Inject() () extends JwtService {

  private lazy val notIntendedBehaviorMessage =
    "If this was not intended behavior, consider enabling the JwtModule in your app by adding " +
      "\"play.modules.enabled += io.flow.play.clients.JwtModule\" to your application.conf"

  Logger.info("NotImplementedJwtService started")

  override def decode(jwt: String): Try[Map[String, String]] = {
    Logger.warn("[NotImplementedJwtService] decode function called. Returning a failure. " + notIntendedBehaviorMessage)
    DefaultJwtService.JwtFailure
  }

  override def encode(claims: Map[String, String]): String = {
    Logger.warn("[NotImplementedJwtService] encode function called. Throwing an exception. " + notIntendedBehaviorMessage)
    DefaultJwtService.JwtFailure.get
  }

}


