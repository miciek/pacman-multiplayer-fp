package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data.{East, Grid, PacMan, Position}

object GridRepository {
  private val smallGrid = gridFromString(
    """
        |###############################
        |#                             #
        |#            #                #
        |##########   ####             #
        |#            #                #
        |#                             #
        |###############################
      """.stripMargin
  )

  val gridByName: String => Grid = _ => smallGrid // more grids soon ;)

  def gridFromString(s: String): Grid = {
    val rows = s.trim.split("\n")
    val usablePositions: Array[Position] = rows.zipWithIndex.flatMap {
      case (row, j) =>
        row.toCharArray.zipWithIndex.flatMap {
          case (c, i) =>
            if (c == ' ') Array(Position(i, j)) else Array.empty[Position]
        }
    }
    Grid(width = rows.head.length,
         height = rows.length,
         usablePositions.toSet,
         PacMan(usablePositions.head, direction = East))
  }
}
