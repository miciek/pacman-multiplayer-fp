package com.michalplachta.pacman

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}
import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("app.host")
  val port = config.getInt("app.port")

  val startNewGame: (ServerState, Grid) => (ServerState, Int) = { (state, grid) =>
    val illegalGameId = -1
    val maybeNewGame = GameEngine.start(grid)
    maybeNewGame.map(game => Server.addNewGame(state, game)).getOrElse((state, illegalGameId))
  }

  val handler = new HttpHandler(
    ServerState.clean,
    startNewGame,
    Server.getPacMan,
    Server.setNewDirection
  )
  handler.startServer(host, port)
}
