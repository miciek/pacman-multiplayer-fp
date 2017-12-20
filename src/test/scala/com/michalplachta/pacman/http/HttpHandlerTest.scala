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
    "allow getting a particular grid configuration" in new MockedHandler() {
      Get("/grids/simpleSmall") ~> handler.route ~> check {
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

    "allow starting a new game in chosen grid configuration" in new MockedHandler() {
      val entity = HttpEntity(`application/json`, s"""{ "gridName": "$validGridName" }""")
      Post("/games", entity) ~> handler.route ~> check {
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

    "not allow starting a new game in unknown grid configuration" in new MockedHandler(1 -> PacMan(Position(0, 0), West)) {
      val entity = HttpEntity(`application/json`, """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting Pac-Man's state in existing game" in new MockedHandler(1 -> PacMan(Position(2, 1), East)) {
      Get("/games/1") ~> handler.route ~> check {
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

    "not allow getting the Pac-Man state when the game is not found" in new MockedHandler(1 -> PacMan(Position(0, 0), East)) {
      Get("/games/2") ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow setting a new direction of Pac-Man" in new MockedHandler(1 -> PacMan(Position(0, 0), East)) {
      val entity = HttpEntity(`application/json`, """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.OK
      }

      Get("/games/1") ~> handler.route ~> check {
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

  private class MockedHandler(games: (Int, PacMan)*) {
    final case class FakeGame(id: Int, pacMan: PacMan)
    final case class FakeState(games: List[FakeGame])

    val newGame = FakeGame(666, PacMan(Position(0, 0), East))
    val validGridName = "validGridName"

    val handler: HttpHandler[FakeState, FakeGame] = new HttpHandler[FakeState, FakeGame](
      FakeState(games.toList.map({ case (id, pacMan) => FakeGame(id, pacMan) })),
      gridName => if(gridName == validGridName) Right(newGame) else Left("Not a valid grid name"),
      g => State(s => (s.copy(games = g :: s.games), g.id)),
      id => State(s => (s, s.games.find(_.id == id))),
      (_, g) => State(_ => (FakeState(List(g)), ())),
      _.pacMan,
      direction => fakeGame => fakeGame.lens(_.pacMan.direction).set(direction),
      identity
    )
  }

  private def beJson(right: String) = new Matcher[String] {
    def apply(left: String) = MatchResult(
      left.parseJson == right.parseJson,
      left + " was not " + right,
      left + " was " + right
    )
  }
}
