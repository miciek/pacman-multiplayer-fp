package com.michalplachta.pacman.http

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import cats.data.State
import com.michalplachta.pacman.game.data._
import monocle.macros.syntax.lens._
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class HttpHandlerTest extends WordSpec with Matchers with ScalatestRouteTest {
  "HTTP Handler" should {
    "allow getting a particular grid configuration" in new TestScope() {
      Get("/grids/simpleSmall") ~> handler.handleGetGrid ~> check {
        contentType shouldEqual `application/json`
        val expected = {
          def c(x: Int, y: Int) = s"""{"x": $x, "y": $y}"""
          s"""
            |{
            |  "width": 3,
            |  "height": 3,
            |  "emptyCells": [${c(1, 1)}, ${c(1, 2)}, ${c(2, 1)}, ${c(2, 2)}],
            |  "initialPacMan": { "position": { "x": 1, "y": 1 }, "direction": "east" },
            |  "initialDotCells": []
            |}
          """.stripMargin
        }

        responseAs[String] should beJson(expected)
      }
    }

    "allow starting a new game in chosen grid configuration" in new TestScope() {
      val startGameRoute = handler.handleStartGame(startNewGame, addGame)

      val entity = HttpEntity(`application/json`, s"""{ "gridName": "$validGridName" }""")
      Post("/games", entity) ~> startGameRoute ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "gameId": ${newGame.id}
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "not allow starting a new game in unknown grid configuration" in new TestScope() {
      val startGameRoute = handler.handleStartGame(startNewGame, addGame)

      val entity = HttpEntity(`application/json`, """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> startGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting Pac-Man's state in existing game" in new TestScope(1 -> PacMan(Position(2, 1), East)) {
      val getGameRoute = handler.handleGetGame(getGameState, getPacMan, identity)
      Get("/games/1") ~> getGameRoute ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "pacMan": {
             |    "position": { "x": 2, "y": 1 },
             |    "direction": "east"
             |  }
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "not allow getting the Pac-Man state when the game is not found" in new TestScope(1 -> PacMan(Position(0, 0), East)) {
      val getGameRoute = handler.handleGetGame(getGameState, getPacMan, identity)
      Get("/games/2") ~> getGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow setting a new direction of Pac-Man" in new TestScope(1 -> PacMan(Position(0, 0), East)) {
      val setDirectionRoute = handler.handleSetDirection(getGameState, setGameState, setDirection)
      val getGameRoute = handler.handleGetGame(getGameState, getPacMan, identity)

      val entity = HttpEntity(`application/json`, """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> setDirectionRoute ~> check {
        status shouldEqual StatusCodes.OK
      }

      Get("/games/1") ~> getGameRoute ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "pacMan": {
             |    "position": { "x": 0, "y": 0 },
             |    "direction": "south"
             |  }
             |}
          """.stripMargin

        val response = responseAs[String]
        response should beJson(expected)
      }
    }
  }

  private class TestScope(games: (Int, PacMan)*) {
    final case class FakeGame(id: Int, pacMan: PacMan)
    final case class FakeState(games: List[FakeGame])

    val newGame = FakeGame(666, PacMan(Position(0, 0), East))
    val validGridName = "validGridName"

    val handler: HttpHandler[FakeState, FakeGame] = new HttpHandler[FakeState, FakeGame](
      FakeState(games.toList.map({ case (id, pacMan) => FakeGame(id, pacMan) }))
    )

    def startNewGame(gridName: String) =
      if(gridName == validGridName) Right(newGame) else Left("Not a valid grid name")

    def addGame(game: FakeGame): State[FakeState, Int] =
      State(s => (s.copy(games = game :: s.games), game.id))

    def getGameState(id: Int): State[FakeState, Option[FakeGame]] =
      State(s => (s, s.games.find(_.id == id)))

    def getPacMan(game: FakeGame): PacMan = game.pacMan

    def setGameState(id: Int, newGameState: FakeGame): State[FakeState, Unit] =
      State(_ => (FakeState(List(newGameState)), ()))

    def setDirection(direction: Direction)(fakeGame: FakeGame) = fakeGame.lens(_.pacMan.direction).set(direction)
  }

  private def beJson(right: String) = new Matcher[String] {
    def apply(left: String) = MatchResult(
      left.parseJson == right.parseJson,
      left + " was not " + right,
      left + " was " + right
    )
  }
}
