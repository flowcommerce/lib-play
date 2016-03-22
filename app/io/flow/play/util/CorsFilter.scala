package io.flow.play.util

import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter


/**
 * Play framework filter that implements Cross-Origin Resource Sharing (or CORS).
 * For more information, see: https://www.playframework.com/documentation/2.4.x/CorsFilter
 *
 * To use in any Flow app depending on lib-play:
 *
 * (1) Add this to your base.conf:
 *    play.http.filters=io.flow.play.util.CorsFilter
 *    play.filters.cors.preflightMaxAge = 3 days
 *    play.filters.cors.allowedHttpMethods = ["GET", "POST", "OPTIONS"] // whatever you allow in your service
 *
 * (2) [Recommended] Add this to your application.production.conf:
 *    play.filters.cors.allowedOrigins = ["https://flow.io", "https://www.flow.io", "https://console.flow.io", "https://api.flow.io"]
 *
 * (3) Note that development will allow all origins for easier development/testing
 *
 **/
class CorsFilter @Inject() (corsFilter: CORSFilter) extends HttpFilters {
  def filters = Seq(corsFilter)
}
