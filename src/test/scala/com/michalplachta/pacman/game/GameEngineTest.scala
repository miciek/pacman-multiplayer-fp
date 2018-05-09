package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

class GameEngineTest extends WordSpec with Matchers {
  "[starting the game] Game engine" should {
    "start the game with specified grid and initial Pac-Man position" in {
      val state = GameEngine.start(
        Grid(
          width = 1,
          height = 1,
          usableCells = Set(Position(0, 0)),
          initialPacMan = PacMan(Position(0, 0), direction = West)
        )
      )

      state.map(_.pacMan.position) should be(Right(Position(0, 0)))
    }

    "not start the game with usable cell positions outside the grid" in {
      val state = GameEngine.start(
        Grid(
          width = 1,
          height = 3,
          usableCells = Set(Position(0, 0), Position(2, 1), Position(-1, -2)),
          initialPacMan = PacMan(Position(0, 0), direction = West)
        )
      )

      state.isLeft should be(true)
    }

    "not start the game with Pac-Man on illegal position" in {
      val state = GameEngine.start(
        Grid(
          width = 2,
          height = 2,
          usableCells = Set(Position(0, 0), Position(0, 1)),
          initialPacMan = PacMan(Position(1, 1), direction = West)
        )
      )

      state.isLeft should be(true)
    }
  }

  "[movement] Game engine" should {
    "move Pac-Man in east direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = East),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }

    "move Pac-Man in west direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = West),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(0, 0))
    }

    "move Pac-Man in north direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = North),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }

    "move Pac-Man in south direction" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = South),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 1))
    }

    "not move Pac-Man into a wall" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = East),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 0))
    }
  }

  "[wrapping] Game engine" should {
    "wrap Pac-Man around the grid (horizontally)" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = East),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(0, 0))
    }

    "wrap Pac-Man around the grid (vertically)" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = North),
                                   None,
                                   grid,
                                   Set.empty)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.pacMan.position should be(Position(1, 1))
    }
  }

  "[rotations] Player" should {
    "be able to rotate and move the Pac-Man east" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = South),
                                   None,
                                   grid,
                                   Set.empty)
      val stateWithChangedDirection =
        GameEngine.changePacMansDirection(East)(initialState)
      val nextState = GameEngine.movePacMan(stateWithChangedDirection)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = East))
    }

    "be able to rotate and move the Pac-Man west" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = South),
                                   None,
                                   grid,
                                   Set.empty)
      val stateWithChangedDirection =
        GameEngine.changePacMansDirection(West)(initialState)
      val nextState = GameEngine.movePacMan(stateWithChangedDirection)
      nextState.pacMan should be(PacMan(Position(0, 0), direction = West))
    }

    "be able to rotate and move the Pac-Man north" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = West),
                                   None,
                                   grid,
                                   Set.empty)
      val stateWithChangedDirection =
        GameEngine.changePacMansDirection(North)(initialState)
      val nextState = GameEngine.movePacMan(stateWithChangedDirection)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = North))
    }

    "be able to rotate and move the Pac-Man south" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(1, 0), direction = West),
                                   None,
                                   grid,
                                   Set.empty)
      val stateWithChangedDirection =
        GameEngine.changePacMansDirection(South)(initialState)
      val nextState = GameEngine.movePacMan(stateWithChangedDirection)
      nextState.pacMan should be(PacMan(Position(1, 1), direction = South))
    }

    "not be able to rotate the Pac-Man into a wall" in new TwoByTwoWithEastWallGrid {
      val initialState = GameState(PacMan(Position(1, 1), direction = North),
                                   None,
                                   grid,
                                   Set.empty)
      val stateWithChangedDirection =
        GameEngine.changePacMansDirection(East)(initialState)
      val nextState = GameEngine.movePacMan(stateWithChangedDirection)
      nextState.pacMan should be(PacMan(Position(1, 0), direction = North))
    }
  }

  "[eating dots] Game engine" should {
    "remove a dot from a cell when Pac-Man enters it (eating a dot)" in new TwoByTwoEmptyGrid {
      val initialState = GameState(PacMan(Position(0, 0), direction = East),
                                   None,
                                   grid,
                                   dotCells = usableCells)
      val nextState = GameEngine.movePacMan(initialState)
      nextState.dotCells should be(usableCells - Position(1, 0))
    }
  }

  trait TwoByTwoEmptyGrid {
    val usableCells =
      Set(Position(0, 0), Position(0, 1), Position(1, 0), Position(1, 1))
    val grid = Grid(width = 2,
                    height = 2,
                    usableCells,
                    PacMan(Position(0, 0), direction = East))
  }

  trait TwoByTwoWithEastWallGrid {
    val usableCells =
      Set(Position(0, 0), Position(0, 1), Position(1, 0), Position(1, 1))
    val grid = Grid(width = 3,
                    height = 2,
                    usableCells,
                    PacMan(Position(0, 0), direction = East))
  }
}
