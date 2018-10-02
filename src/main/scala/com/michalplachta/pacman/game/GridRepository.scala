package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}

object GridRepository {
  private val smallGrid = {
    val usableCells: Set[Position] = (for {
      x <- 1 to 28
      y <- 1 to 18
    } yield Position(x, y)).toSet
    Grid(width = 30, height = 20, usableCells, PacMan(Position(15, 10), direction = East))
  }

  val gridByName: String => Grid = _ => smallGrid // more grids soon ;)
}
