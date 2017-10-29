package com.michalplachta.pacman.server

import java.time.Clock

import com.michalplachta.pacman.game.data._
import com.michalplachta.pacman.server.Server.ServerState
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

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

    "not change the game state before a defined tick duration passes" in new ServerWithOneGame(PacMan(Position(1, 1), direction = East)) {
      import TickInstants._
      val newState: ServerState = Server.tick(state, currentTime = instantBeforeChange)
      newState should be(state)
    }
  }

  class ServerWithOneGame(pacMan: PacMan) {
    val gameId = 3
    val state = Map(gameId -> GameState(pacMan, Grid.simpleSmall, Set.empty))
  }

  object TickInstants {
    val tickDuration = 1.second
    val initialInstant = Clock.systemUTC().instant()
    val instantBeforeChange = initialInstant.plusMillis((tickDuration / 2).toMillis)
    val instantAfterChange = initialInstant.plusMillis((tickDuration * 2).toMillis)
  }
}
