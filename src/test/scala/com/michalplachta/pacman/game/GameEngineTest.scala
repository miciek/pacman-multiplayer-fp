package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

class GameEngineTest extends WordSpec with Matchers {
  "Game engine (grid setup)" should {
    "start the game with specified grid and initial Pac-Man position" in {
      val state = GameEngine.start(
        Grid(width = 1, height = 1, emptyCells = Set(Position(0, 0))),
        initialPacMan = PacMan(Position(0, 0), direction = West)
      )

      state.map(_.pacMan.position) should be(Some(Position(0, 0)))
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

  "Game engine (movement)" should {
    "move Pac-Man in east direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = East), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }

    "move Pac-Man in west direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = West), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(0, 0))
    }

    "move Pac-Man in north direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = North), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }

    "move Pac-Man in south direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = South), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 1))
    }

    "not move Pac-Man into a wall" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = East), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }

    "wrap Pac-Man around the grid (horizontally)" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = East), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(0, 0))
    }

    "wrap Pac-Man around the grid (vertically)" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = North), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 1))
    }
  }

  "Player" should {
    "be able to rotate and move the Pac-Man east" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = South, nextDirection = Some(East)), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = East))
    }

    "be able to rotate and move the Pac-Man west" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = South, nextDirection = Some(West)), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan should be(PacMan(Position(0, 0), direction = West))
    }

    "be able to rotate and move the Pac-Man north" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = West, nextDirection = Some(North)), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan should be(PacMan(Position(1, 1), direction = North))
    }

    "be able to rotate and move the Pac-Man south" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = West, nextDirection = Some(South)), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = South))
    }

    "not be able to rotate the Pac-Man into a wall" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = North, nextDirection = Some(North)), grid)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = North))
    }
  }

  "Game engine (dots)" should {
    "allow an empty cell to contain a dot inside" in {
      val state = GameEngine.start(
        Grid(width = 2, height = 2, emptyCells = Set(Position(0, 0))),
        initialPacMan = PacMan(Position(0, 0), direction = West),
        initialDotCells = Set(Position(0, 0))
      )

      state.map(_.dotCells) should be(Some(Set(Position(0, 0))))
    }

    "not allow a cell which is not empty to contain a dot inside" in {
      val state = GameEngine.start(
        Grid(width = 2, height = 2, emptyCells = Set(Position(0, 0))),
        initialPacMan = PacMan(Position(0, 0), direction = West),
        initialDotCells = Set(Position(1, 0))
      )

      state should be(None)
    }

    "remove a dot from a cell when Pac-Man enters it (eating a dot)" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = East), grid, dotCells = emptyCells)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.dotCells should be(emptyCells - Position(1, 0))
    }
  }

  trait TwoByTwoEmptyGrid {
    val emptyCells = Set(Position(0, 0), Position(0, 1), Position(1, 0), Position(1, 1))
    val grid = Grid(width = 2, height = 2, emptyCells)
  }

  trait TwoByTwoWithEastWallGrid {
    val emptyCells = Set(Position(0, 0), Position(0, 1), Position(1, 0), Position(1, 1))
    val grid = Grid(width = 3, height = 2, emptyCells)
  }
}
