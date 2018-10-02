package com.michalplachta.pacman

import akka.http.scaladsl.server.HttpApp
import com.typesafe.config.ConfigFactory
import monix.execution.Scheduler
import akka.http.scaladsl.server._
import scala.concurrent.duration._

object Main extends App {
  val config = ConfigFactory.load()
  val host   = config.getString("app.host")
  val port   = config.getInt("app.port")
  val tickDuration =
    Duration.fromNanos(config.getDuration("app.tick-duration").toNanos)

  val server = new StatefulHttpRoute(Scheduler.singleThread(name = "tick-games-thread"), tickDuration)
  val httpApp = new HttpApp {
    override protected def routes: Route =
      Route.seal(pathPrefix("backend") { server.route })
  }
  httpApp.startServer(host, port)
}
