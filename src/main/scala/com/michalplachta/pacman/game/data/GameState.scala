package com.michalplachta.pacman.game.data

case class GameState(pacMan: PacMan, nextPacManDirection: Option[Direction], grid: Grid, dotCells: Set[Position])
