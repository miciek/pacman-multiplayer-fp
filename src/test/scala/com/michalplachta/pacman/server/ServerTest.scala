package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data._
import com.michalplachta.pacman.server.Server.ServerState
import org.scalatest.{Matchers, WordSpec}

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "allow starting a new game" in {
      val state = Server.cleanState
      val gameStateToAdd = GameState(PacMan(Position(0, 0), East), Grid.simpleSmall, Set.empty)
      val (newState, newGameId): (ServerState, Int) = Server.addNewGame(state, gameStateToAdd)
      newState.get(newGameId).isDefined shouldEqual true
    }

    "allow getting Pac-Man state" in new ServerWithOneGame(PacMan(Position(2,2), direction = North)) {
      val maybePacMan = Server.getPacMan(state, gameId)
      maybePacMan should contain(PacMan(Position(2, 2), direction = North))
    }

    "allow setting Pac-Man direction" in new ServerWithOneGame(PacMan(Position(2,2), direction = North)) {
      val newState = Server.setNewDirection(state, gameId, newDirection = South)
      newState.get(gameId).flatMap(_.pacMan.nextDirection) should contain(South)
    }
  }

  class ServerWithOneGame(pacMan: PacMan) {
    val gameId = 3
    val state = Map(gameId -> GameState(pacMan, Grid.simpleSmall, Set.empty))
  }
}
