package com.michalplachta.pacman.server

import java.time.Instant

import cats.data.State

import scala.concurrent.duration.Duration

object Server {
  def addNewGame[G](g: G) = State { state: ServerState[G] =>
    val gameId = state.games.size
    val newState = ServerState(state.games + (gameId -> g), state.lastTicked)
    (newState, gameId)
  }

  def getGameFromState[G](gameId: Int) = State { state: ServerState[G] =>
    (state, state.games.get(gameId))
  }

  def updateGameInState[G](gameId: Int, g: G) = State { state: ServerState[G] =>
    (ServerState(state.games.updated(gameId, g), state.lastTicked), ())
  }

  def tick[G](currentTime: Instant, tickDuration: Duration, tickF: G => G) = State { state: ServerState[G] =>
    val nextTick = state.lastTicked.plusMillis(tickDuration.toMillis)
    if(currentTime.isBefore(nextTick)) {
      (state, ())
    } else {
      (ServerState(state.games.mapValues(tickF), lastTicked = currentTime), ())
    }
  }
}
