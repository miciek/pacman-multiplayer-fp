package com.michalplachta.pacman.entangled

import akka.http.scaladsl.server.{HttpApp, Route}
import com.typesafe.config.ConfigFactory

/**
  * WARNING!
  *
  * This class demonstrates _WRONG_ approach to building HTTP APIs that
  * entangles concerns: state is entangled inside HTTP layer.
  *
  * See [[com.michalplachta.pacman.Main]] to see better approach.
  */
object EntangledMain extends App {
  val config = ConfigFactory.load()
  val host   = config.getString("app.host")
  val port   = config.getInt("app.port")

  val statefulRoutes = new EntangledStatefulHttpRoutes
  val httpApp = new HttpApp {
    override protected def routes: Route = statefulRoutes.routes
  }
  httpApp.startServer(host, port)
}
