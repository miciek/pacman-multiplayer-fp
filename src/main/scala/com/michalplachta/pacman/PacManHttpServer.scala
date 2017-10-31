package com.michalplachta.pacman

import java.time.Clock

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data.{Direction, GameState, Grid, PacMan}
import com.michalplachta.pacman.http.HttpHandler
import com.michalplachta.pacman.server.{Server, ServerState}

import scala.concurrent.duration._

class PacManHttpServer(clock: Clock, tickDuration: Duration) {
  private val startNewGame: (ServerState[GameState], String) => Either[String, (ServerState[GameState], Int)] = {
    (state, gridName) =>
      val maybeGrid = Grid.fromName(gridName)
      val newGameOrError = maybeGrid.map(GameEngine.start).getOrElse(Left(s"Grid with the name $gridName not found"))
      newGameOrError.map(game => Server.addNewGame(state, game))
  }

  private val getPacManWithStateUpdate: (ServerState[GameState], Int) => (ServerState[GameState], Option[PacMan]) = {
    (state, gameId) =>
      val updatedState = Server.tick(state, clock.instant(), tickDuration, GameEngine.movePacMan)
      (updatedState, Server.getGameFromState(updatedState, gameId).map(_.pacMan))
  }

  private val changePacMansDirectionInState: (ServerState[GameState], Int, Direction) => ServerState[GameState] = {
    (state, gameId, newDirection) =>
      Server.updateGameInState(state, gameId, GameEngine.changePacMansDirection(_, newDirection))
  }

  val httpHandler = new HttpHandler[ServerState[GameState]](
    ServerState.clean(clock.instant()),
    startNewGame,
    getPacManWithStateUpdate,
    changePacMansDirectionInState
  )
}
