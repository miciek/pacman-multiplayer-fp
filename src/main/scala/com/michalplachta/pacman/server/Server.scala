package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.{Direction, North, PacMan, Position}

object Server {
  def startNewGame(state: ServerState): (ServerState, Int) = {
    val gameId = state.nextGameId
    val newState = state.copy(
      games = state.games + ServerGame(gameId, 0, PacMan(Position(0, 0), direction = North)),
      nextGameId = gameId + 1
    )
    (newState, gameId)
  }

  def changeDirection(state: ServerState, gameId: Int, newDirection: Direction): ServerState = {
    val otherGames: Set[ServerGame] = state.games.filterNot(_.id == gameId)
    val maybeGame: Option[ServerGame] = state.games.find(_.id == gameId)
    val updatedGame = maybeGame.map(game => game.copy(pacMan = game.pacMan.copy(direction = newDirection)))
    state.copy(games = otherGames ++ updatedGame.toSet)
  }

  def tick(state: ServerState): ServerState = state
}
