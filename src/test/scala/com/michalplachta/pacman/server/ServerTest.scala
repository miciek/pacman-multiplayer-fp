package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "allow starting a new game" in {
      val serverState = ServerState.clean
      val (newState, _) = Server.startNewGame(serverState)
      newState.games.size should be(1)
    }

    "allow starting a new game with different id than previous one" in {
      val serverState = ServerState.clean
      val (stateWithOneGame, firstGameId) = Server.startNewGame(serverState)
      val (_, secondGameId) = Server.startNewGame(stateWithOneGame)
      firstGameId should not be secondGameId
    }

    "allow changing direction of Pac-Man" in new StateWithOneGame(PacMan(Position(0, 0), direction = East)) {
      val stateWithChangedDirection = Server.changeDirection(state, gameId, newDirection = South)
      stateWithChangedDirection.games.size should be(1)
      stateWithChangedDirection.games.head.gameState.pacMan.direction should be (East)

      val newState = Server.tick(stateWithChangedDirection)
      newState.games.size should be(1)
      newState.games.head.gameState.pacMan.direction should be (South)
    }
  }

  private class StateWithOneGame(pacMan: PacMan) {
    private val gameState: GameState = GameState(pacMan, Grid.noWalls, Set.empty)
    val gameId = 1
    val state: ServerState = ServerState(Set.empty, Set(ServerGame(gameId, gameState)), nextGameId = gameId + 1)
  }
}
