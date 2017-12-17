package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import monocle.macros.syntax.lens._

object GameEngine {
  def start(grid: Grid): Either[String, GameState] = {
    if(isGridValid(grid)) {
      Right(GameState(grid.initialPacMan, grid, grid.initialDotCells))
    } else Left("Grid is not valid")
  }

  def movePacMan(gameState: GameState): GameState = {
    val oldPosition = gameState.pacMan.position
    val newDirection = gameState.pacMan.nextDirection.getOrElse(gameState.pacMan.direction)
    val newPosition = newDirection match {
      case West => moveAndWrap(oldPosition, gameState.grid, dx = -1, dy = 0)
      case East => moveAndWrap(oldPosition, gameState.grid, dx = 1, dy = 0)
      case North => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = -1)
      case South => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = 1)
    }

    if(isPositionLegal(gameState.grid, newPosition) && newPosition != oldPosition)
      gameState.copy(
        pacMan = PacMan(newPosition, newDirection, None),
        dotCells = gameState.dotCells - newPosition
      )
    else gameState
  }

  def changePacMansDirection(gameState: GameState, newDirection: Direction): GameState = {
    gameState.lens(_.pacMan.nextDirection).set(Some(newDirection))
  }

  def moveAndWrap(position: Position, grid: Grid, dx: Int, dy: Int): Position = {
    Position((position.x + dx + grid.width) % grid.width, (position.y + dy + grid.height) % grid.height)
  }

  def isGridValid(grid: Grid): Boolean = {
    isGridSizeValid(grid) &&
    isPositionLegal(grid, grid.initialPacMan.position) &&
    areDotCellsLegal(grid, grid.initialDotCells)
  }

  def isGridSizeValid(grid: Grid): Boolean = {
    grid.width > 0 && grid.height > 0 && grid.emptyCells.forall(cell => cell.x < grid.width && cell.y < grid.height)
  }

  def isPositionLegal(grid: Grid, position: Position): Boolean = {
    grid.emptyCells.contains(position)
  }

  def areDotCellsLegal(grid: Grid, dotCells: Set[Position]): Boolean = {
    dotCells.forall(dotCell => grid.emptyCells.contains(dotCell))
  }
}
