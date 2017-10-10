package com.michalplachta.pacman.server

object Server {
  def startNewGame(serverState: ServerState): ServerState = {
    val gameId = serverState.nextGameId
    serverState.copy(games = serverState.games + ServerGame(gameId, 0), nextGameId = gameId + 1)
  }
}
