package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import monocle.macros.syntax.lens._

object GameEngine {
  def start(grid: Grid): Either[String, GameState] = {
    if (isGridValid(grid)) {
      Right(GameState(grid.initialPacMan, None, grid))
    } else Left("Grid is not valid")
  }

  def movePacMan(gameState: GameState): GameState = {
    val oldPosition = gameState.pacMan.position
    val newDirection =
      gameState.nextPacManDirection.getOrElse(gameState.pacMan.direction)

    def wrap(position: Position): Position = {
      val Grid(width, height, _, _) = gameState.grid
      Position((position.x + width) % width, (position.y + height) % height)
    }
    val newPosition = wrap(newDirection match {
      case East  => oldPosition.copy(x = oldPosition.x + 1)
      case West  => oldPosition.copy(x = oldPosition.x - 1)
      case North => oldPosition.copy(y = oldPosition.y - 1)
      case South => oldPosition.copy(y = oldPosition.y + 1)
    })

    if (isPositionLegal(gameState.grid, newPosition))
      gameState.copy(pacMan = PacMan(newPosition, newDirection))
    else if (newDirection != gameState.pacMan.direction)
      movePacMan(changePacMansDirection(gameState.pacMan.direction)(gameState))
    else gameState
  }

  def changePacMansDirection(newDirection: Direction)(gameState: GameState): GameState = {
    gameState.lens(_.nextPacManDirection).set(Some(newDirection))
  }

  def isGridValid(grid: Grid): Boolean = {
    isGridSizeValid(grid) &&
    isPositionLegal(grid, grid.initialPacMan.position)
  }

  def isGridSizeValid(grid: Grid): Boolean = {
    grid.width > 0 && grid.height > 0 && grid.usableCells.forall(cell => cell.x < grid.width && cell.y < grid.height)
  }

  def isPositionLegal(grid: Grid, position: Position): Boolean = {
    grid.usableCells.contains(position)
  }
}
