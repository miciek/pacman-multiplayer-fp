package com.michalplachta.pacman.http

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.michalplachta.pacman.game.http.Server
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}
import spray.json._

class ServerTest extends WordSpec with Matchers with ScalatestRouteTest {
  "Server" should {
    "allow getting simple small grid configuration" in {
      Get("/grid/simpleSmall") ~> Server.route ~> check {
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
  }

  private def beJson(right: String) = new Matcher[String] {
    def apply(left: String) = MatchResult(
      left.parseJson == right.parseJson,
      left + " was not " + right,
      left + " was " + right
    )
  }
}
