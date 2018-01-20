package com.michalplachta.pacman.state

import com.michalplachta.pacman.game.data.{Direction, GameState}
import monocle.macros.syntax.lens._
import monix.execution.atomic.Atomic

class MultipleGamesAtomicState {
  private val atomicMap = Atomic(Map.empty[Int, GameState])

  def addNewGame(game: GameState): Int =
    atomicMap.transformAndExtract { state =>
      val gameId = state.size
      val newState = state + (gameId -> game)
      (gameId, newState)
    }

  def getGame(gameId: Int): Option[GameState] =
    atomicMap.get.get(gameId)

  def setGame(gameId: Int, game: GameState): Unit =
    atomicMap.transform(_.updated(gameId, game))

  def setDirection(direction: Direction)(gameState: GameState) =
    gameState.lens(_.nextPacManDirection).set(Some(direction))

  def tickAllGames(tickF: GameState => GameState): Unit =
    atomicMap.transform(_.mapValues(tickF))
}
