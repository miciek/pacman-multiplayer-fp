package com.michalplachta.pacman.http

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.michalplachta.pacman.game.data._
import com.michalplachta.pacman.http.HttpRoutesTest.FakeGame
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class HttpRoutesTest extends WordSpec with Matchers with ScalatestRouteTest {
  "HTTP Handlers" should {
    "allow creating a new game in chosen grid configuration" in {
      val newGame = FakeGame(666, PacMan(Position(0, 0), East))
      val createGameRoute =
        HttpRoutes.createGameRoute[FakeGame](_ => Right(newGame), _.id)

      val entity =
        HttpEntity(`application/json`, s"""{ "gridName": "validGridName" }""")
      Post("/games", entity) ~> createGameRoute ~> check {
        contentType shouldEqual `application/json`
        val expected =
          s"""
             |{
             |  "gameId": 666
             |}
          """.stripMargin

        responseAs[String] should beJson(expected)
      }
    }

    "not allow creating a new game in an unknown grid configuration" in {
      val createGameRoute = HttpRoutes
        .createGameRoute[FakeGame](_ => Left("Not a valid grid name"), _.id)

      val entity =
        HttpEntity(`application/json`,
                   """{ "gridName": "non existing grid configuration" }""")
      Post("/games", entity) ~> createGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow getting Pac-Man's state in an existing game" in {
      val fakeGame = FakeGame(1, PacMan(Position(2, 1), East))
      val getGameRoute: Route =
        HttpRoutes.getGameRoute[FakeGame](_ => Some(fakeGame), _.pacMan)

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

    "not allow getting the Pac-Man's state in an unknown game" in {
      val getGameRoute: Route =
        HttpRoutes.getGameRoute[FakeGame](_ => None, _.pacMan)

      Get("/games/2") ~> getGameRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "allow setting a new direction of Pac-Man" in {
      val setDirectionRoute = HttpRoutes.setDirectionRoute[FakeGame](
        _ => Some(FakeGame(1, PacMan(Position(0, 0), East))),
        (_, _) => (),
        _ => game => game
      )

      val entity = HttpEntity(`application/json`,
                              """{ "step": 0, "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> setDirectionRoute ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "not allow setting a new direction of Pac-Man when the game is not found" in {
      val setDirectionRoute = HttpRoutes
        .setDirectionRoute[FakeGame](_ => None, (_, _) => (), _ => game => game)

      val entity =
        HttpEntity(`application/json`, """{ "newDirection": "south" }""")
      Put("/games/1/direction", entity) ~> setDirectionRoute ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }

  private def beJson(right: String) = new Matcher[String] {
    def apply(left: String) = MatchResult(
      left.parseJson == right.parseJson,
      left + " was not " + right,
      left + " was " + right
    )
  }
}

object HttpRoutesTest {
  final case class FakeGame(id: Int, pacMan: PacMan)
}
