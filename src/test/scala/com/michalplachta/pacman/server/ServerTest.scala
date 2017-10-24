package com.michalplachta.pacman.server

import com.michalplachta.pacman.game.data.North
import org.scalatest.{Matchers, WordSpec}

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "save the new direction in state" in {
      val (state, gameId) = Server.startNewGame(Map.empty)
      val newState = Server.setNewDirection(state, gameId, North)
      Server.getPacMan(newState, gameId).flatMap(_.nextDirection) should contain(North)
    }
  }
}
