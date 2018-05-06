package com.michalplachta.pacman.state

object MultipleGamesState {
  def addGame[G](game: G)(state: Map[Int, G]): (Int, Map[Int, G]) = {
    val gameId = state.size
    val newState = state + (gameId -> game)
    (gameId, newState)
  }

  def getGame[G](gameId: Int)(state: Map[Int, G]): Option[G] =
    state.get(gameId)

  def updateGame[G](gameId: Int, game: G)(state: Map[Int, G]): Map[Int, G] =
    state.updated(gameId, game)

  def tickAllGames[G](tick: G => G)(state: Map[Int, G]): Map[Int, G] =
    state.mapValues(tick)
}
