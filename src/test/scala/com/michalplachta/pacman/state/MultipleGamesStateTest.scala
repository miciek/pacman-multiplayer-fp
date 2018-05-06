package com.michalplachta.pacman.state

import com.michalplachta.pacman.game.{GameEngine, GridRepository}
import com.michalplachta.pacman.game.data._
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

class MultipleGamesStateTest extends WordSpec with Matchers with GivenWhenThen {
  "Multiple Games State" should {
    "allow adding a new game" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, GameState]

      When("a new game is added")
      val newGame = GameState(PacMan(Position(0, 0), East),
                              None,
                              GridRepository.smallGrid,
                              Set.empty)
      val (gameId, newState) =
        MultipleGamesState.addGame(newGame)(emptyState)

      Then("state should contain this game")
      MultipleGamesState.getGame(gameId)(newState) should contain(newGame)
    }

    "allow updating an existing game" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, GameState]

      And("one added game")
      val newGame = GameState(PacMan(Position(0, 0), East),
                              None,
                              GridRepository.smallGrid,
                              Set.empty)
      val (gameId, stateWithGame) =
        MultipleGamesState.addGame(newGame)(emptyState)

      When("the game is updated")
      val updatedGame = GameState(PacMan(Position(1, 0), West),
                                  None,
                                  GridRepository.smallGrid,
                                  Set.empty)
      val finalState =
        MultipleGamesState.updateGame(gameId, updatedGame)(stateWithGame)

      Then("state should contain updated game")
      MultipleGamesState.getGame(gameId)(finalState) should contain(updatedGame)
    }

    "update all games on tick" in {
      Given("an empty state")
      val emptyState = Map.empty[Int, GameState]

      And("two added games")
      val newGame1 = GameState(PacMan(Position(0, 0), East),
                               None,
                               GridRepository.smallGrid,
                               Set.empty)
      val newGame2 = GameState(PacMan(Position(0, 0), South),
                               None,
                               GridRepository.smallGrid,
                               Set.empty)
      val (gameId1, state1) =
        MultipleGamesState.addGame(newGame1)(emptyState)
      val (gameId2, state2) =
        MultipleGamesState.addGame(newGame2)(state1)

      When("all games are updated through tick")
      val finalState =
        MultipleGamesState.tickAllGames(GameEngine.movePacMan)(state2)

      Then("state should contain updated games")
      MultipleGamesState.getGame(gameId1)(finalState) should contain(
        newGame1.copy(pacMan = newGame1.pacMan.copy(Position(1, 0))))
      MultipleGamesState.getGame(gameId2)(finalState) should contain(
        newGame1.copy(pacMan = newGame2.pacMan.copy(Position(0, 1))))
    }
  }
}
