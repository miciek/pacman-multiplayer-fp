package com.michalplachta.pacman.http

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.michalplachta.pacman.game.data._
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class HttpHandlerTest extends WordSpec with Matchers with ScalatestRouteTest {
  "HTTP Handler" should {
    "allow getting a particular grid configuration" in new HandlerWithNoGame {
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

    "allow starting a new game in chosen grid configuration" in new HandlerWithOneGameState(gameId = 1, PacMan(Position(1, 1), East)) {
      val entity = HttpEntity(`application/json`, """{ "gridName": "simpleSmall" }""")
      Post("/games", entity) ~> handler.route ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "gameId": 2
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "not allow starting a new game in unknown grid configuration" in new HandlerWithNoGame {
      val entity = HttpEntity(`application/json`, """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting Pac-Man's state in existing game" in new HandlerWithOneGameState(gameId = 1, PacMan(Position(2, 1), East)) {
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

    "not allow getting the Pac-Man state when there's no game" in new HandlerWithNoGame {
      Get("/games/1") ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow setting a new direction of Pac-Man" in new HandlerWithDirectionState(Position(0, 0), initialDirection = East) {
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

        responseAs[String] should beJson(expected)
      }
    }
  }

  private trait HandlerWithNoGame {
    val handler = new HttpHandler[Int](0, (s, _) => (s + 1, s + 1), (s, _) => (s, None), (s, _, _) => s)
  }

  private class HandlerWithOneGameState(gameId: Int, pacMan: PacMan) {
    val handler = new HttpHandler[Int](
      gameId,
      (_, _) => (gameId, gameId + 1),
      (s, id) => if(id == gameId) (s, Some(pacMan)) else (s, None),
      (s, _, _) => s
    )
  }

  private class HandlerWithDirectionState(position: Position, initialDirection: Direction) {
    val handler = new HttpHandler[Direction](
      initialDirection,
      (_, _) => (initialDirection, 0),
      (direction, _) => (direction, Some(PacMan(position, direction))),
      (_, _, newDirection) => newDirection
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
