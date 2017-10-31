package com.michalplachta.pacman.server

import java.time.Instant

import scala.concurrent.duration.Duration

object Server {
  def addNewGame[G](state: ServerState[G], gameState: G): (ServerState[G], Int) = {
    val gameId = state.games.size
    val newState = ServerState(state.games + (gameId -> gameState), state.lastTicked)
    (newState, gameId)
  }

  def getGameFromState[G](state: ServerState[G], gameId: Int): Option[G] = {
    state.games.get(gameId)
  }

  def updateGameInState[G](state: ServerState[G], gameId: Int, f: G => G): ServerState[G] = {
    val updatedGame: Option[G] = state.games.get(gameId).map(f)
    updatedGame.map(game => ServerState(state.games.updated(gameId, game), state.lastTicked)).getOrElse(state)
  }

  def tick[G](state: ServerState[G], currentTime: Instant, tickDuration: Duration, f: G => G): ServerState[G] = {
    val nextTick = state.lastTicked.plusMillis(tickDuration.toMillis)
    if(currentTime.isBefore(nextTick)) {
      state
    } else {
      ServerState(state.games.mapValues(f), lastTicked = currentTime)
    }
  }
}
