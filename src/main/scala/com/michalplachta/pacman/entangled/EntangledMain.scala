package com.michalplachta.pacman.entangled

import akka.http.scaladsl.server.HttpApp
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * This class demonstrated approach to building HTTP APIs that
  * entangles concerns.
  *
  * [[com.michalplachta.pacman.Main]] demonstrates better approach.
  */
object EntangledMain extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")
  val tickDuration =
    Duration.fromNanos(config.getDuration("app.tick-duration").toNanos)

  val statefulRoutes = new StatefulHttpRoutes
  val httpApp = new HttpApp {
    override protected def routes = statefulRoutes.routes
  }
  httpApp.startServer(host, port)
}
