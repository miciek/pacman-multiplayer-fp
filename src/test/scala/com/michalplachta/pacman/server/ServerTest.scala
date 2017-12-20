package com.michalplachta.pacman.server

import java.time.Clock

import com.michalplachta.pacman.server.ServerTest.FakeGame
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

class ServerTest extends WordSpec with Matchers {
  "Server" should {
    "allow starting a new game" in {
      val initialState = ServerState.clean[FakeGame](Clock.systemUTC().instant())
      val (newState, newGameId) = Server.addNewGame(FakeGame("state")).run(initialState).value
      newState.games.get(newGameId) should not be empty
    }

    "allow getting game state" in new ServerWithOneGame {
      val game = Server.getGameFromState(fakeGameId).runA(state).value
      game should contain(fakeGame)
    }

    "allow setting updated game state" in new ServerWithOneGame {
      val newFakeGame = FakeGame("newFakeGame")
      val newState = Server.updateGameInState(fakeGameId, newFakeGame).runS(state).value
      newState.games.get(fakeGameId) should contain(newFakeGame)
    }

    "not change the game state before the defined tick duration passes" in new ServerWithOneGame {
      val newState = Server.tick(currentTime = beforeTick, tickDuration, tickF).runS(state).value
      newState should be(state)
    }

    "change the game state after the defined tick duration passes" in new ServerWithOneGame {
      val newState = Server.tick(currentTime = afterTick, tickDuration, tickF).runS(state).value
      newState.games.get(fakeGameId) should contain(fakeGameAfterTick)
    }
  }

  trait ServerWithOneGame {
    val fakeGameId = 3
    val fakeGame = FakeGame("game3")
    val fakeGameAfterTick = FakeGame("game3ticked")

    val startedTime = Clock.systemUTC().instant()
    val tickDuration = 1.second
    val beforeTick = startedTime.plusMillis((tickDuration / 2).toMillis)
    val afterTick = startedTime.plusMillis((tickDuration * 2).toMillis)

    val tickF: FakeGame => FakeGame = _ => fakeGameAfterTick

    val state = ServerState(Map(fakeGameId -> fakeGame), startedTime)
  }
}

object ServerTest {
  final case class FakeGame(s: String)
}