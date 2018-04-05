package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import monocle.macros.syntax.lens._
import eu.timepit.refined.auto._

object GameEngine {
  def start(grid: Grid): Either[String, GameState] = {
    if (isGridValid(grid)) {
      Right(GameState(grid.initialPacMan, None, grid, grid.usableCells))
    } else Left("Grid is not valid")
  }

  def movePacMan(gameState: GameState): GameState = {
    val oldPosition = gameState.pacMan.position
    val newDirection =
      gameState.nextPacManDirection.getOrElse(gameState.pacMan.direction)
    val newPosition = newDirection match {
      case West  => moveAndWrap(oldPosition, gameState.grid, dx = -1, dy = 0)
      case East  => moveAndWrap(oldPosition, gameState.grid, dx = 1, dy = 0)
      case North => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = -1)
      case South => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = 1)
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

  def moveAndWrap(position: Position,
                  grid: Grid,
                  dx: Int,
                  dy: Int): Position = {
    Position((position.x + dx + grid.width) % grid.width,
             (position.y + dy + grid.height) % grid.height)
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
