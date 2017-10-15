package com.michalplachta.pacman.server

import org.scalatest.{Matchers, WordSpec}

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "allow starting a new game" in {
      val serverState = ServerState.clean
      val (newState, _) = Server.startNewGame(serverState)
      newState.games.size should be(1)
    }

    "allow starting a new game with different id than previous one" in {
      val serverState = ServerState.clean
      val (stateWithOneGame, firstGameId) = Server.startNewGame(serverState)
      val (_, secondGameId) = Server.startNewGame(stateWithOneGame)
      firstGameId should not be secondGameId
    }
  }
}
