package com.michalplachta.pacman.server

import java.time.Clock

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "allow starting a new game" in {
      val state = ServerState.clean
      val gameStateToAdd = GameState(PacMan(Position(0, 0), East), Grid.simpleSmall, Set.empty)
      val (newState, newGameId): (ServerState, Int) = Server.addNewGame(state, gameStateToAdd)
      newState.games.get(newGameId).isDefined shouldEqual true
    }

    "allow getting Pac-Man state" in new ServerWithOneGame(PacMan(Position(2,2), direction = North)) {
      val maybePacMan = Server.getPacMan(state, gameId)
      maybePacMan should contain(PacMan(Position(2, 2), direction = North))
    }

    "allow setting Pac-Man direction" in new ServerWithOneGame(PacMan(Position(2,2), direction = North)) {
      val newState = Server.setNewDirection(state, gameId, newDirection = South)
      newState.games.get(gameId).flatMap(_.pacMan.nextDirection) should contain(South)
    }

    "not change the game state before the defined tick duration passes" in new ServerWithOneGame(PacMan(Position(1, 1), direction = East)) {
      val newState: ServerState = Server.tick(state, currentTime = instantBeforeChange, tickDuration, tickF)
      newState should be(state)
    }

    "change the game state after the defined tick duration passes" in new ServerWithOneGame(PacMan(Position(1, 1), direction = East)) {
      val newState: ServerState = Server.tick(state, currentTime = instantAfterChange, tickDuration, tickF)
      newState.games.get(gameId).map(_.pacMan) should contain(pacManAfterTick)
    }
  }

  class ServerWithOneGame(pacMan: PacMan) {
    val gameId = 3
    val initialTick = Clock.systemDefaultZone().instant()
    val state = ServerState(Map(gameId -> GameState(pacMan, Grid.simpleSmall, Set.empty)), initialTick)

    val tickDuration = 1.second
    val instantBeforeChange = initialTick.plusMillis((tickDuration / 2).toMillis)
    val instantAfterChange = initialTick.plusMillis((tickDuration * 2).toMillis)

    val pacManAfterTick = pacMan.copy(position = Position(pacMan.position.x + 666, pacMan.position.y + 777))
    val tickF: GameState => GameState = { game =>
      game.copy(pacMan = pacManAfterTick)
    }
  }
}
