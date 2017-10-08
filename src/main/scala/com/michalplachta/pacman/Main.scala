package com.michalplachta.pacman

import com.michalplachta.pacman.http.HttpHandler
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")
  new HttpHandler().startServer(host, port)
}
