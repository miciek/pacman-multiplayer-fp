package com.michalplachta.pacman.state

import com.michalplachta.pacman.game.data.GameState

object MultipleGamesState {
  def addGame(game: GameState)(
      state: Map[Int, GameState]): (Int, Map[Int, GameState]) = {
    val gameId = state.size
    val newState = state + (gameId -> game)
    (gameId, newState)
  }

  def getGame(gameId: Int)(state: Map[Int, GameState]): Option[GameState] =
    state.get(gameId)

  def updateGame(gameId: Int, game: GameState)(
      state: Map[Int, GameState]): Map[Int, GameState] =
    state.updated(gameId, game)

  def tickAllGames(tickF: GameState => GameState)(
      state: Map[Int, GameState]): Map[Int, GameState] =
    state.mapValues(tickF)
}
