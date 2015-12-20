package io.flow.play.controllers

import java.util.UUID

trait FlowControllerHelpers {

  /**
   * Even if not specified, play router passed in Some(Nil) as opposed
   * to None. Here we return None if there is no list or the list is
   * empty.
   */
  def optionals[T](guids: Option[Seq[T]]): Option[Seq[T]] = {
    guids match {
      case None => None
      case Some(values) => {
        values match {
          case Nil => None
          case ar => Some(ar)
        }
      }
    }
  }

}
