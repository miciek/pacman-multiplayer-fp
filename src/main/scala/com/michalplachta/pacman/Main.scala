package com.michalplachta.pacman

import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")
  new HttpHandler(ServerState.clean,
                  Server.startNewGame,
                  Server.getCurrentStep,
                  Server.getPacMan
                 ).startServer(host, port)
}
