package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}

object GridRepository {
  val smallGrid = {
    val usableCells: Set[Position] = (for {
      x <- 0 to 2
      y <- 0 to 2
    } yield Position(x, y)).toSet
    Grid(width = 3,
         height = 3,
         usableCells,
         PacMan(Position(0, 0), direction = East))
  }

  val gridByName: String => Grid = _ => smallGrid // more grids soon ;)
}
