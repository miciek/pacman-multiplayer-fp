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
    "allow getting a particular grid configuration" in new TestScope {
      Get("/grids/simpleSmall") ~> HttpHandler.handleGetGrid ~> check {
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

    "allow creating a new game in chosen grid configuration" in new TestScope {
      val createGameRoute = HttpHandler.handleCreateGame(createGame, addGame)

      val entity = HttpEntity(`application/json`, s"""{ "gridName": "$validGridName" }""")
      Post("/games", entity) ~> createGameRoute ~> check {
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

    "not allow creating a new game in unknown grid configuration" in new TestScope {
      val createGameRoute = HttpHandler.handleCreateGame(createGame, addGame)

      val entity = HttpEntity(`application/json`, """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> createGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting Pac-Man's state in existing game" in new TestScope {
      val getGameRoute = HttpHandler.handleGetGame(_ => Some(FakeGame(1, PacMan(Position(2, 1), East))), getPacMan)

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

    "not allow getting the Pac-Man state when the game is not found" in new TestScope {
      val getGameRoute = HttpHandler.handleGetGame(_ => None, getPacMan)

      Get("/games/2") ~> getGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow setting a new direction of Pac-Man" in new TestScope {
      val setDirectionRoute = HttpHandler.handleSetDirection[FakeGame](
        _ => Some(FakeGame(1, PacMan(Position(0, 0), East))),
        (_, _) => (),
        setDirection
      )

      val entity = HttpEntity(`application/json`, """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> setDirectionRoute ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "not allow setting a new direction of Pac-Man when the game is not found" in new TestScope {
      val setDirectionRoute = HttpHandler.handleSetDirection[FakeGame](_ => None, (_, _) => (), setDirection)

      val entity = HttpEntity(`application/json`, """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> setDirectionRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  trait TestScope {
    final case class FakeGame(id: Int, pacMan: PacMan)
    final case class FakeState(games: List[FakeGame])

    val newGame = FakeGame(666, PacMan(Position(0, 0), East))
    val validGridName = "validGridName"

    def createGame(gridName: String) =
      if(gridName == validGridName) Right(newGame) else Left("Not a valid grid name")

    def addGame(game: FakeGame): Int = game.id

    def getPacMan(game: FakeGame): PacMan = game.pacMan

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
