package io.flow.play.controllers

import io.flow.healthcheck.v0.models.Healthcheck
import io.flow.healthcheck.v0.models.json._

import play.api.mvc._
import play.api.libs.json._

trait Healthchecks extends BaseController {

  def getInternalAndHealthcheck = Action { _ =>
    Ok(Json.toJson(Healthcheck(status = status())))
  }

  def status(): String = "healthy"

}
