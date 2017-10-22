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
      games = state.games + ServerGame(gameId, gameState),
      nextGameId = gameId + 1
    ))
    (maybeNewState.getOrElse(state), gameId)
  }

  def findServerGame(state: ServerState, gameId: Int): Option[ServerGame] = {
    state.games.find(_.id == gameId)
  }

  // TODO: use function composition
  def getPacMan(state: ServerState, gameId: Int): Option[PacMan] = {
    val game = findServerGame(state, gameId)
    game.map(_.gameState.pacMan)
  }

  def changeDirection(state: ServerState, gameId: Int, newDirection: Direction): ServerState = {
    state.copy(games = updateOneGame(state.games, gameId) { game =>
      game.copy(gameState = GameEngine.rotatePacMan(game.gameState, newDirection))
    })
  }

  def tick(state: ServerState): ServerState = {
    state.copy(
      games = state.games.map { game =>
        game.copy(
          gameState = GameEngine.movePacMan(game.gameState)
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
