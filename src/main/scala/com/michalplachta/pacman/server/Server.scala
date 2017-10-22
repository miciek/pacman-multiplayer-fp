package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data._

// TODO: use State
// TODO: use optics
object Server {
  def startNewGame(state: ServerState): (ServerState, Int) = {
    val gameId = state.nextGameId // TODO returns improper value when game cannot be started
    val maybeGameState: Option[GameState] = GameEngine.start(Grid.simpleSmall, PacMan(Position(1, 1), East, None), Set.empty)
    val maybeNewState = maybeGameState.map(gameState => state.copy(
      games = state.games + ServerGame(gameId, currentStep = 0, gameState),
      nextGameId = gameId + 1
    ))
    (maybeNewState.getOrElse(state), gameId)
  }

  def changeDirection(state: ServerState, gameId: Int, newDirection: Direction): ServerState = {
    state.copy(games = updateOneGame(state.games, gameId) { game =>
      game.copy(gameState = game.gameState.copy(pacMan = game.gameState.pacMan.copy(nextDirection = Some(newDirection))))
    })
  }

  def tick(state: ServerState): ServerState = {
    state.copy(
      games = state.games.map { game =>
        game.copy(
          gameState = game.gameState.copy(pacMan = game.gameState.pacMan.copy(direction = game.gameState.pacMan.nextDirection.getOrElse(game.gameState.pacMan.direction), nextDirection = None)),
          currentStep = game.currentStep + 1
        )
      })
  }

  private def updateOneGame(games: Set[ServerGame], gameId: Int)(f: ServerGame => ServerGame): Set[ServerGame] = {
    val otherGames: Set[ServerGame] = games.filterNot(_.id == gameId)
    val maybeGame: Option[ServerGame] = games.find(_.id == gameId)
    val updatedGame = maybeGame.map(f)
    otherGames ++ updatedGame.toSet
  }
}
