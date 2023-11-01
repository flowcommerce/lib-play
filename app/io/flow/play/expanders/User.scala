package io.flow.play.expanders

import io.flow.common.v0.models.UserReference
import io.flow.play.util.Expander
import play.api.libs.json._
import scala.concurrent.{Future, ExecutionContext}
import io.flow.common.v0.models.json._

/*
  User 'Expander' work by:
  1. Generate list of all User Ids from the passed in JsValue
  2. Query User API with just those User Ids that may be expandable
  3. For each returned (valid) User, if an expanded User exists, return it, otherwise, return the UserReference as is
 */
case class User(
  fieldName: String,
  userClient: io.flow.user.v0.interfaces.Client
) extends Expander {

  def expand(records: Seq[JsValue], requestHeaders: Seq[(String, String)] = Nil)(implicit
    ec: ExecutionContext
  ): Future[Seq[JsValue]] = {
    val userIds: Seq[String] = records.map { r =>
      ((r \ fieldName).validate[UserReference]: @unchecked) match {
        case JsSuccess(userReference, _) => userReference.id
      }
    }

    userIds match {
      case Nil => Future.successful(records)
      case ids => {
        userClient.users
          .get(id = Some(ids), limit = userIds.size.toLong, requestHeaders = requestHeaders)
          .map(users => Map(users.map(user => user.id -> user): _*))
          .map(userIdLookup =>
            records.map { r =>
              r.validate[JsObject] match {
                case JsSuccess(obj, _) => {
                  (r \ fieldName).validate[UserReference] match {
                    case JsSuccess(userReference, _) => {
                      obj ++ Json.obj(
                        fieldName ->
                          (userIdLookup.get(userReference.id) match { // getOrElse can't be used to serialize multiple types - no formatter
                            case Some(user) => Json.toJson(user)
                            case None => Json.toJson(userReference)
                          })
                      )
                    }
                    case JsError(_) => r
                  }
                }
                case JsError(_) => r
              }
            }
          )
      }
    }
  }
}
