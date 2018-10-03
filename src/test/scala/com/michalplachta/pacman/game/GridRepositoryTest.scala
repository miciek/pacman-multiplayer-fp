package com.michalplachta.pacman.game

import com.michalplachta.pacman.game.data._
import org.scalatest.{Matchers, WordSpec}

class GridRepositoryTest extends WordSpec with Matchers {
  "Grid generator" should {
    "generate proper walls around grid defined in String" in {
      val grid = GridRepository.gridFromString(
        """
          |####
          |#  #
          |####
        """.stripMargin
      )

      grid should be(Grid(4, 3, Set(Position(1, 1), Position(2, 1)), PacMan(Position(1, 1), direction = East)))
    }
  }
}
