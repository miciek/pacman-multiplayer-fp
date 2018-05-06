package com.michalplachta.pacman.state

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class MultipleGamesStateTest extends WordSpec with Matchers with GivenWhenThen {
  "Multiple Games State" should {
    "allow adding a new game" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, String]

      When("a new game is added")
      val newGame = "game"
      val (gameId, newState) =
        MultipleGamesState.addGame(newGame)(emptyState)

      Then("state should contain this game")
      MultipleGamesState.getGame(gameId)(newState) should contain(newGame)
    }

    "allow updating an existing game" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, String]

      And("one added game")
      val newGame = "game"
      val (gameId, stateWithGame) =
        MultipleGamesState.addGame(newGame)(emptyState)

      When("the game is updated")
      val updatedGame = "updatedGame"
      val finalState =
        MultipleGamesState.updateGame(gameId, updatedGame)(stateWithGame)

      Then("state should contain updated game")
      MultipleGamesState.getGame(gameId)(finalState) should contain(updatedGame)
    }

    "update all games on tick" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, String]

      And("two added games")
      val (gameId1, state1) =
        MultipleGamesState.addGame("game1")(emptyState)
      val (gameId2, state2) =
        MultipleGamesState.addGame("game2")(state1)

      When("all games are updated through tick")
      val finalState =
        MultipleGamesState.tickAllGames[String](_ + "-updated")(state2)

      Then("state should contain updated games")
      MultipleGamesState.getGame(gameId1)(finalState) should contain(
        "game1-updated")
      MultipleGamesState.getGame(gameId2)(finalState) should contain(
        "game2-updated")
    }
  }
}
