package io.flow.play.util

import authentikat.jwt.JsonWebToken
import play.api.Logger

case class Salts(config: Config) {

  private[this] val VarName = "JWT_SALTS"

  /**
    * This is the list of ALL salts that may be in use. Guaranteed to be
    * non empty
    */
  lazy val all: List[String] = {
    val salts = if (config.optionalString(VarName).isDefined) {
      config.requiredString(VarName).
        trim.split("\\s+").map(_.trim).
        filter(_.nonEmpty).toList
    } else {
      Logger.warn("[io.flow.play.util.Salts] Using old JWT_SALT environment variable")
      List(config.requiredString("JWT_SALT"))
    }

    assert(
      salts.nonEmpty,
      s"Must have at least one value in the $VarName environment variable"
    )

    salts
  }

  /**
    * This is the preferred salt to use when creating a jwt header
    */
  lazy val preferred: String = all.last

  def isJsonWebTokenValid(token: String): Boolean = {
    all.find { s =>
      JsonWebToken.validate(token, s)
    } match {
      case None => {
        false
      }

      case Some(salt) => {
        if (salt != preferred) {
          Logger.warn("[io.flow.play.util.Salts] Verified JWT token using old salt")
        }
        true
      }
    }
  }
}