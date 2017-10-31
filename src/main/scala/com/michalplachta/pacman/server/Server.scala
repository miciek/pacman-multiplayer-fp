package com.michalplachta.pacman.server

import java.time.Instant

import com.michalplachta.pacman.game.data._

import scala.concurrent.duration.Duration

object Server {
  def addNewGame(state: ServerState[GameState], gameState: GameState): (ServerState[GameState], Int) = {
    val gameId = state.games.size
    val newState = ServerState(state.games + (gameId -> gameState), state.lastTicked)
    (newState, gameId)
  }

  def getPacMan(state: ServerState[GameState], gameId: Int): Option[PacMan] = {
    state.games.get(gameId).map(_.pacMan)
  }

  def setNewDirection(state: ServerState[GameState], gameId: Int, newDirection: Direction, f: (GameState, Direction) => GameState): ServerState[GameState] = {
    val updatedGame: Option[GameState] = state.games.get(gameId).map(f(_, newDirection))
    updatedGame.map(game => ServerState(state.games.updated(gameId, game), state.lastTicked)).getOrElse(state)
  }

  def tick(state: ServerState[GameState], currentTime: Instant, tickDuration: Duration, f: GameState => GameState): ServerState[GameState] = {
    val nextTick = state.lastTicked.plusMillis(tickDuration.toMillis)
    if(currentTime.isBefore(nextTick)) {
      state
    } else {
      ServerState(state.games.mapValues(f), lastTicked = currentTime)
    }
  }
}
