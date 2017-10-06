package com.michalplachta.pacman.game.data

case class GameState(pacMan: PacMan, grid: Grid, dotCells: Set[Position] = Set.empty)
