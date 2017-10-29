package com.michalplachta.pacman.server

import java.time.Instant

import com.michalplachta.pacman.game.data._

import scala.concurrent.duration.Duration

object Server {
  def addNewGame(state: ServerState, gameState: GameState): (ServerState, Int) = {
    val gameId = state.games.size
    val newState = ServerState(state.games + (gameId -> gameState), state.lastTicked)
    (newState, gameId)
  }

  def getPacMan(state: ServerState, gameId: Int): Option[PacMan] = {
    state.games.get(gameId).map(_.pacMan)
  }

  def setNewDirection(state: ServerState, gameId: Int, newDirection: Direction): ServerState = {
    val updatedGame: Option[GameState] =
      state.games
        .get(gameId)
        .map(game =>
          game.copy(
            pacMan = game.pacMan.copy(nextDirection = Some(newDirection))
          )
        )
    updatedGame.map(game => ServerState(state.games.updated(gameId, game), state.lastTicked)).getOrElse(state)
  }

  def tick(state: ServerState, currentTime: Instant, tickDuration: Duration, f: GameState => GameState): ServerState = {
    val nextTick = state.lastTicked.plusMillis(tickDuration.toMillis)
    if(currentTime.isBefore(nextTick)) {
      state
    } else {
      ServerState(state.games.mapValues(f), lastTicked = currentTime)
    }
  }
}
