package com.michalplachta.pacman

import java.time.Clock

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")
  val tickDuration = Duration.fromNanos(config.getDuration("app.tick-duration").toNanos)

  new PacManHttpServer(Clock.systemDefaultZone(), tickDuration).httpHandler.startServer(host, port)
}
