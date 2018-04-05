package com.michalplachta.pacman.game.data

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive

final case class Grid(width: Int Refined Positive,
                      height: Int Refined Positive,
                      emptyCells: Set[Position],
                      initialPacMan: PacMan,
                      initialDotCells: Set[Position])
