package com.michalplachta.pacman.state

import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class MultipleGamesAtomicStateTest
    extends WordSpec
    with Matchers
    with GivenWhenThen {
  "Multiple Games State" should {
    "allow adding a new game" in { // LIVE CODING
      Given("an empty state")
      val state = new MultipleGamesAtomicState

      When("a new game is added")
      val gameId: Int = ??? // state.addNewGame(...)

      Then("state should contain this game")
      state.getGame(gameId) should contain(???)
    }

    "allow updating an existing game" in { // TODO #6
      Given("an empty state")
      val state = new MultipleGamesAtomicState

      And("one added game")
      val gameId: Int = ??? // state.addNewGame(...)

      When("the game is updated")
      // state.setNewGame

      Then("state should contain updated game")
      state.getGame(gameId) should contain(???)
    }

    "update all games on tick" in { // TODO #7
      Given("an empty state")
      val state = new MultipleGamesAtomicState

      And("two added games")
      val gameId1: Int = ??? // state.addNewGame(...)
      val gameId2: Int = ??? // state.addNewGame(...)

      When("all games are updated through tick")
      // state.tickAllGames

      Then("state should contain updated games")
      state.getGame(gameId1) should contain(???)
      state.getGame(gameId2) should contain(???)
    }
  }
}
