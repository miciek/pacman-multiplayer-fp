package com.michalplachta.pacman

import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.Server
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")

  val handler = new HttpHandler(Server.cleanState, Server.startNewGame, Server.getPacMan, Server.setNewDirection)
  handler.startServer(host, port)
}
