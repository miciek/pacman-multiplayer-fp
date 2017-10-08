package com.michalplachta.pacman.server

object Server {
  def startNewGame(serverState: ServerState): ServerState = {
    serverState.copy(games = serverState.games + ServerGame(1, 0))
  }
}
