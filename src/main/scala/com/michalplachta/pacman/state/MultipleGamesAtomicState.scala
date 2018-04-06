package com.michalplachta.pacman.state

import com.michalplachta.pacman.game.data.{Direction, GameState}
import monix.execution.atomic.Atomic

class MultipleGamesAtomicState {
  private val atomicMap = Atomic(Map.empty[Int, GameState])

  def addNewGame(game: GameState): Int =
    ???

  def getGame(gameId: Int): Option[GameState] =
    atomicMap.get.get(gameId)

  def setGame(gameId: Int, game: GameState): Unit =
    ???

  def setDirection(direction: Direction)(gameState: GameState): GameState =
    gameState.copy(nextPacManDirection = Some(direction))

  def tickAllGames(tickF: GameState => GameState): Unit =
    ???
}
