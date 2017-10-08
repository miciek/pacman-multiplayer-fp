package com.michalplachta.pacman.http

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class HttpHandlerTest extends WordSpec with Matchers with ScalatestRouteTest {
  "HTTP Handler" should {
    "allow getting a particular grid configuration" in new HandlerWithStartedGame {
      Get("/grids/simpleSmall") ~> handler.route ~> check {
        contentType shouldEqual `application/json`
        val expected = {
          def c(x: Int, y: Int) = s"""{"x": $x, "y": $y}"""
          s"""
            |{
            |  "width": 3,
            |  "height": 3,
            |  "emptyCells": [${c(1, 1)}, ${c(1, 2)}, ${c(2, 1)}, ${c(2, 2)}]
            |}
          """.stripMargin
        }

        responseAs[String] should beJson(expected)
      }
    }

    "allow starting a new game in chosen grid configuration" in new HandlerWithStartedGame {
      val entity = HttpEntity(`application/json`, """{ "gridName": "simpleSmall" }""")
      Post("/games", entity) ~> handler.route ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "gameId": 1
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "not allow starting a new game in unknown grid configuration" in new HandlerWithStartedGame {
      val entity = HttpEntity(`application/json`, """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting the Pac-Man state in a game with given id" in new HandlerWithStartedGame {
      Get("/games/1") ~> handler.route ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "step": 0,
             |  "pacMan": {
             |    "position": { "x": 1, "y": 1 },
             |    "direction": "east"
             |  }
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "allow setting a new direction of Pac-Man in a game with given id" in new HandlerWithStartedGame {
      val entity = HttpEntity(`application/json`, """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1", entity) ~> handler.route ~> check {
        contentType shouldEqual `text/plain(UTF-8)`
        status shouldEqual StatusCodes.OK
      }
    }
  }

  trait HandlerWithStartedGame {
    val handler = new HttpHandler()
  }

  private def beJson(right: String) = new Matcher[String] {
    def apply(left: String) = MatchResult(
      left.parseJson == right.parseJson,
      left + " was not " + right,
      left + " was " + right
    )
  }
}