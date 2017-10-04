package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._

object GameEngine {
  def start(grid: Grid, initialPacMan: PacMan): Option[GameState] = {
    if(isGridValid(grid) && isPositionLegal(grid, initialPacMan.position)) {
      Some(GameState(initialPacMan, grid))
    } else None
  }

  def tick(gameState: GameState): GameState = {
    val newPosition = gameState.pacMan.direction match {
      case West => Position(gameState.pacMan.position.x - 1, gameState.pacMan.position.y)
      case East => Position(gameState.pacMan.position.x + 1, gameState.pacMan.position.y)
      case North => Position(gameState.pacMan.position.x, gameState.pacMan.position.y - 1)
      case South => Position(gameState.pacMan.position.x, gameState.pacMan.position.y + 1)
    }

    if(isPositionLegal(gameState.grid, newPosition))
      gameState.copy(pacMan = PacMan(newPosition, gameState.pacMan.direction))
    else gameState
  }

  private def isGridValid(grid: Grid) = {
    grid.width > 0 && grid.height > 0 && grid.emptyCells.forall(cell => cell.x < grid.width && cell.y < grid.height)
  }

  private def isPositionLegal(grid: Grid, position: Position) = {
    grid.emptyCells.contains(position)
  }
}
