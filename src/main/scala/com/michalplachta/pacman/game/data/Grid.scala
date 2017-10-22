package com.michalplachta.pacman.game.data

case class Grid(width: Int, height: Int, emptyCells: Set[Position])

object Grid {
  val simpleSmall = {
    val emptyCells: Set[Position] = (for {
      x <- 1 to 2
      y <- 1 to 2
    } yield Position(x, y)).toSet
    Grid(width = 3, height = 3, emptyCells)
  }
}
