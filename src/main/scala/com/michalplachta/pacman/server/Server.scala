package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.{North, PacMan, Position}

object Server {
  def startNewGame(serverState: ServerState): ServerState = {
    val gameId = serverState.nextGameId
    serverState.copy(
      games = serverState.games + ServerGame(gameId, 0, PacMan(Position(0, 0), direction = North)),
      nextGameId = gameId + 1
    )
  }
}
