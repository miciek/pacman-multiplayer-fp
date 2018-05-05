package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import monocle.macros.syntax.lens._

object GameEngine {
  def start(grid: Grid): Either[String, GameState] = {
    if (isGridValid(grid)) {
      Right(GameState(grid.initialPacMan, None, grid, grid.usableCells))
    } else Left("Grid is not valid")
  }

  def movePacMan(gameState: GameState): GameState = {
    def moveAndWrap(position: Position, dx: Int, dy: Int): Position = {
      val Grid(width, height, _, _) = gameState.grid
      Position((position.x + dx + width) % width,
               (position.y + dy + height) % height)
    }

    val oldPosition = gameState.pacMan.position
    val newDirection =
      gameState.nextPacManDirection.getOrElse(gameState.pacMan.direction)
    val newPosition = newDirection match {
      case West  => moveAndWrap(oldPosition, dx = -1, dy = 0)
      case East  => moveAndWrap(oldPosition, dx = 1, dy = 0)
      case North => moveAndWrap(oldPosition, dx = 0, dy = -1)
      case South => moveAndWrap(oldPosition, dx = 0, dy = 1)
    }

    if (isPositionLegal(gameState.grid, newPosition) && newPosition != oldPosition)
      gameState.copy(
        pacMan = PacMan(newPosition, newDirection),
        nextPacManDirection = None,
        dotCells = gameState.dotCells - newPosition
      )
    else gameState
  }

  def changePacMansDirection(gameState: GameState,
                             newDirection: Direction): GameState = {
    gameState.lens(_.nextPacManDirection).set(Some(newDirection))
  }

  def isGridValid(grid: Grid): Boolean = {
    isGridSizeValid(grid) &&
    isPositionLegal(grid, grid.initialPacMan.position)
  }

  def isGridSizeValid(grid: Grid): Boolean = {
    grid.width > 0 && grid.height > 0 && grid.usableCells.forall(cell =>
      cell.x < grid.width && cell.y < grid.height)
  }

  def isPositionLegal(grid: Grid, position: Position): Boolean = {
    grid.usableCells.contains(position)
  }
}
