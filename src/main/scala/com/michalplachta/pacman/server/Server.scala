package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.GameEngine
import com.michalplachta.pacman.game.data._

object Server {
  type ServerState = Map[Int, GameState]
  private val illegalGameId = -1

  def startNewGame(state: ServerState): (ServerState, Int) = {
    val gameId = state.size
    val maybeGame = GameEngine.start(Grid.simpleSmall, PacMan(Position(1, 1), East, nextDirection = None))
    val newState = maybeGame.map(game => state + (gameId -> game)).getOrElse(state)
    (newState, maybeGame.map(_ => gameId).getOrElse(illegalGameId))
  }

  def getPacMan(state: ServerState, gameId: Int): Option[PacMan] = {
    state.get(gameId).map(_.pacMan)
  }
}
