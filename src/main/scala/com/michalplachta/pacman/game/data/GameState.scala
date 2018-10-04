package com.michalplachta.pacman.game.data

final case class GameState(pacMan: PacMan, nextPacManDirection: Option[Direction], grid: Grid)
