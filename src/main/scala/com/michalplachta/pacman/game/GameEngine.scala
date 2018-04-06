package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._

object GameEngine {
  def start(grid: Grid): Either[String, GameState] = {
    // TODO #1
    ???
  }

  def movePacMan(gameState: GameState): GameState = {
    val oldPosition = gameState.pacMan.position
    val newDirection =
      gameState.nextPacManDirection.getOrElse(gameState.pacMan.direction)
    val newPosition = newDirection match {
      case West  => oldPosition.copy(x = oldPosition.x - 1)
      case East  => oldPosition.copy(x = oldPosition.x + 1)
      case North => oldPosition.copy(y = oldPosition.y - 1)
      case South => oldPosition.copy(y = oldPosition.y + 1)
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
    gameState.copy(nextPacManDirection = Some(newDirection))
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
