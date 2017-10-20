package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.{Direction, North, PacMan, Position}

// TODO: use State
// TODO: use optics
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
    state.copy(games = updateOneGame(state.games, gameId) { game =>
      game.copy(pacMan = game.pacMan.copy(nextDirection = Some(newDirection)))
    })
  }

  def tick(state: ServerState): ServerState = {
    state.copy(games = state.games.map { game =>
      game.copy(pacMan = game.pacMan.copy(direction = game.pacMan.nextDirection.getOrElse(game.pacMan.direction), nextDirection = None))
    })
  }

  private def updateOneGame(games: Set[ServerGame], gameId: Int)(f: ServerGame => ServerGame): Set[ServerGame] = {
    val otherGames: Set[ServerGame] = games.filterNot(_.id == gameId)
    val maybeGame: Option[ServerGame] = games.find(_.id == gameId)
    val updatedGame = maybeGame.map(f)
    otherGames ++ updatedGame.toSet
  }
}
