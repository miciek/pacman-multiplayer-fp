package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.{North, PacMan, Position}

object Server {
  def startNewGame(state: ServerState): (ServerState, Int) = {
    val gameId = state.nextGameId
    val newState = state.copy(
      games = state.games + ServerGame(gameId, 0, PacMan(Position(0, 0), direction = North)),
      nextGameId = gameId + 1
    )
    (newState, gameId)
  }
}
