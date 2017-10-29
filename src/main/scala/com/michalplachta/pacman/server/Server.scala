package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data._

object Server {
  type ServerState = Map[Int, GameState]

  val cleanState: ServerState = Map.empty

  def addNewGame(state: ServerState, gameState: GameState): (ServerState, Int) = {
    val gameId = state.size
    val newState = state + (gameId -> gameState)
    (newState, gameId)
  }

  def getPacMan(state: ServerState, gameId: Int): Option[PacMan] = {
    state.get(gameId).map(_.pacMan)
  }

  def setNewDirection(state: ServerState, gameId: Int, newDirection: Direction): ServerState = {
    val updatedGame: Option[GameState] =
      state
        .get(gameId)
        .map(game =>
          game.copy(
            pacMan = game.pacMan.copy(nextDirection = Some(newDirection))
          )
        )
    updatedGame.map(game => state.updated(gameId, game)).getOrElse(state)
  }
}
