package com.michalplachta.pacman

import java.time.Clock

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{Direction, Grid, PacMan}
import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}

import scala.concurrent.duration._

class PacManHttpServer(clock: Clock, tickDuration: Duration) {
  private val startNewGame: (ServerState, Grid) => (ServerState, Int) = { (state, grid) =>
    val illegalGameId = -1
    val maybeNewGame = GameEngine.start(grid)
    maybeNewGame.map(game => Server.addNewGame(state, game)).getOrElse((state, illegalGameId))
  }

  private val getPacManWithStateUpdate: (ServerState, Int) => (ServerState, Option[PacMan]) = { (state, gameId) =>
    val updatedState = Server.tick(state, clock.instant(), tickDuration, GameEngine.movePacMan)
    (updatedState, Server.getPacMan(updatedState, gameId))
  }

  val httpHandler = new HttpHandler(
    ServerState.clean,
    startNewGame,
    getPacManWithStateUpdate,
    Server.setNewDirection(_: ServerState, _: Int, _: Direction, GameEngine.changePacMansDirection)
  )
}
