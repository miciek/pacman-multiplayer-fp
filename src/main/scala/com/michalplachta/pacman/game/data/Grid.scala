package com.michalplachta.pacman.game.data

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.auto._

final case class Grid(width: Int Refined Positive,
                      height: Int Refined Positive,
                      emptyCells: Set[Position],
                      initialPacMan: PacMan,
                      initialDotCells: Set[Position])

object Grid {
  val small = {
    val emptyCells: Set[Position] = (for {
      x <- 1 to 2
      y <- 1 to 2
    } yield Position(x, y)).toSet
    Grid(width = 3,
         height = 3,
         emptyCells,
         PacMan(Position(1, 1), direction = East),
         Set.empty)
  }
}
