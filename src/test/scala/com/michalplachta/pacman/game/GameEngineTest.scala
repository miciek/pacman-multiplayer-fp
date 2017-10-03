package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

class GameEngineTest extends WordSpec with Matchers {
  "Game engine" should {
    "start the game with specified grid and initial Pac-Man position" in {
      val state = GameEngine.start(
        Grid(width = 1, height = 1, emptyCells = Set(Position(0, 0))),
        initialPacMan = PacMan(Position(0, 0), direction = West)
      )

      state.map(_.pacMan.position) should be(Some(Position(0,0)))
    }

    "not start the game with illegal grid" in {
      val state = GameEngine.start(
        Grid(width = 0, height = 1, emptyCells = Set(Position(0, 0))),
        initialPacMan = PacMan(Position(0, 0), direction = West)
      )

      state should be(None)
    }

    "not start the game with empty positions outside the grid" in {
      val state = GameEngine.start(
        Grid(width = 1, height = 3, emptyCells = Set(Position(0, 0), Position(2, 1), Position(-1, -2))),
        initialPacMan = PacMan(Position(0, 0), direction = West)
      )

      state should be(None)
    }

    "not start the game with Pac-Man on illegal position" in {
      val state = GameEngine.start(
        Grid(width = 2, height = 2, emptyCells = Set(Position(0, 0), Position(0, 1))),
        initialPacMan = PacMan(Position(1, 1), direction = West)
      )

      state should be(None)
    }
  }
}
