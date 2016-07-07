package io.flow.play.controllers

import authentikat.jwt.{JsonWebToken, JwtClaimsSetJValue}
import io.flow.common.v0.models.{Role, UserReference}
import io.flow.play.util.{AuthData, Config}
import play.api.mvc.{Headers, Session}

import scala.concurrent.{ExecutionContext, Future}

trait UserFromFlowAuth extends UserFromAuthorizationToken {

  def config: Config

  private[this] lazy val jwtSalt = config.requiredString("JWT_SALT")
  
  override def user(
    session: Session,
    headers: Headers,
    path: String,
    queryString: Map[String, Seq[String]]
  ) (
    implicit ec: ExecutionContext
  ): Future[Option[UserReference]] = {
    headers.get("X-Flow-Auth") match {
      case None => {
        // Delegate for now pending upgrade of library
        super.user(session, headers, path, queryString)
      }

      case Some(value) => Future {
        value match {
          case JsonWebToken(header, claimsSet, signature) if jwtIsValid(value) => parseJwtToken(claimsSet).map { data =>
            UserReference(data.userId)
          }
          case _ => None
        }
      }
    }
  }

  private[this] def jwtIsValid(token: String): Boolean = JsonWebToken.validate(token, jwtSalt)

  private[this] def parseJwtToken(claimsSet: JwtClaimsSetJValue): Option[AuthData] = {
    claimsSet.asSimpleMap.toOption.flatMap { claims =>
      claims.get("user_id").map { userId =>
        AuthData(
          userId = userId,
          organization = claims.get("organization"),
          role = claims.get("role").map { Role(_) }
        )
      }
    }
  }

}
