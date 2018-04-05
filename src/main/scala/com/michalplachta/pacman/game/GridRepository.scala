package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}
import eu.timepit.refined.auto._

object GridRepository {
  val smallGrid = {
    val usableCells: Set[Position] = (for {
      x <- 1 to 2
      y <- 1 to 2
    } yield Position(x, y)).toSet
    Grid(width = 3,
         height = 3,
         usableCells,
         PacMan(Position(1, 1), direction = East))
  }

  val gridByName: String => Grid = _ => smallGrid // more grids soon ;)
}
