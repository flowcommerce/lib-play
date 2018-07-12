package io.flow.play.util

import io.flow.common.v0.models.UserReference

@deprecated("Deprecated in favour of lib-util (io.flow.util.*)", "0.4.78")
object Constants {

  val SystemUser = UserReference("usr-20151006-1")
  val AnonymousUser = UserReference("usr-20151006-2")
  val DefaultTaxName = "VAT"
  val FlowOrganizationId = "flow"

  object Prefixes {
    val Session = "F51"
  }

}
