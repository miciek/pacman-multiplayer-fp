package com.michalplachta.pacman.game.data

final case class Grid(width: Int, height: Int, usableCells: Set[Position], initialPacMan: PacMan)
