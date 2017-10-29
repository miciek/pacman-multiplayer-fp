package com.michalplachta.pacman

import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")

  new PacManHttpServer().httpHandler.startServer(host, port)
}
