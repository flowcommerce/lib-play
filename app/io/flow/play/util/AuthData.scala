package io.flow.play.util

import io.flow.common.v0.models.Role

/**
  * Represents the data securely authenticated by the API proxy
  * server. All of our software should depend on data from this object
  * when retrieved from the X-Flow-Auth header (as opposed, for
  * example, to relying on the organization id from the URL path).
  * 
  * The API Proxy server validates this data, and also guarantees that
  * the user is authorized to access information for the specified
  * organization.
  */
case class AuthData(
  userId: String,
  organization: Option[String],
  role: Option[Role]
)

