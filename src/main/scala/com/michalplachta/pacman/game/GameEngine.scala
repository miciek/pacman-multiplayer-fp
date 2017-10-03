package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data.{GameState, Grid, PacMan, Position}

object GameEngine {
  def start(grid: Grid, initialPacMan: PacMan): Option[GameState] = {
    if(isGridLegal(grid) && grid.emptyCells.contains(initialPacMan.position)) {
      Some(GameState(initialPacMan, grid))
    } else None
  }

  private def isGridLegal(grid: Grid) = {
    grid.width > 0 && grid.height > 0 && grid.emptyCells.forall(cell => cell.x < grid.width && cell.y < grid.height)
  }
}
