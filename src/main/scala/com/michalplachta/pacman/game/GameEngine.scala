package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._

object GameEngine {
  def start(grid: Grid, initialPacMan: PacMan): Option[GameState] = {
    if(isGridValid(grid) && isPositionLegal(grid, initialPacMan.position)) {
      Some(GameState(initialPacMan, grid))
    } else None
  }

  def tick(gameState: GameState): GameState = {
    val oldPosition = gameState.pacMan.position
    val newPosition = gameState.pacMan.direction match {
      case West => moveAndWrap(oldPosition, gameState.grid, dx = -1, dy = 0)
      case East => moveAndWrap(oldPosition, gameState.grid, dx = 1, dy = 0)
      case North => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = -1)
      case South => moveAndWrap(oldPosition, gameState.grid, dx = 0, dy = 1)
    }

    if(isPositionLegal(gameState.grid, newPosition))
      gameState.copy(pacMan = PacMan(newPosition, gameState.pacMan.direction))
    else
      gameState
  }

  def rotatePacMan(gameState: GameState, newDirection: Direction): GameState = {
    val possiblyNewGameState = tick(gameState.copy(pacMan = PacMan(gameState.pacMan.position, newDirection)))
    if(possiblyNewGameState.pacMan.position != gameState.pacMan.position) {
      possiblyNewGameState
    } else {
      tick(gameState)
    }
  }

  def isGridValid(grid: Grid): Boolean = {
    grid.width > 0 && grid.height > 0 && grid.emptyCells.forall(cell => cell.x < grid.width && cell.y < grid.height)
  }

  def isPositionLegal(grid: Grid, position: Position): Boolean = {
    grid.emptyCells.contains(position)
  }

  def moveAndWrap(position: Position, grid: Grid, dx: Int, dy: Int): Position = {
    Position((position.x + dx + grid.width) % grid.width, (position.y + dy + grid.height) % grid.height)
  }
}
