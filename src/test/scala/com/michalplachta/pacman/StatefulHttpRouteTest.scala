package com.michalplachta.pacman

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.michalplachta.pacman.game.data._
import com.michalplachta.pacman.http.{NewDirectionRequest, PacManStateResponse, StartGameRequest, StartGameResponse}
import org.scalatest.{GivenWhenThen, Matchers, WordSpec}

import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import com.michalplachta.pacman.http.DirectionAsJson._
import monix.execution.schedulers.TestScheduler

import scala.concurrent.duration._

class StatefulHttpRouteTest extends WordSpec with Matchers with ScalatestRouteTest with GivenWhenThen {
  "[integration test] Stateful HTTP Route" should {
    "support the full happy path" in {
      Given("fully configured handler from Pac-Man HTTP Server")
      val tickDuration = 1.second
      val scheduler    = TestScheduler()
      val route        = new StatefulHttpRoute(scheduler, tickDuration).route

      When("a new game is started")
      val startGameRequest = StartGameRequest(gridName = "small")
      val gameId: Int =
        (Post("/games", startGameRequest) ~> route ~> check {
          responseAs[StartGameResponse]
        }).gameId

      Then("Pac-Man state can be retrieved")
      val pacManAfterStart: PacMan =
        (Get(s"/games/$gameId") ~> route ~> check {
          responseAs[PacManStateResponse]
        }).pacMan

      When("tick duration passes")
      scheduler.tick(tickDuration)

      Then("Pac-Man state changes")
      val pacManAfterTick: PacMan =
        (Get(s"/games/$gameId") ~> route ~> check {
          responseAs[PacManStateResponse]
        }).pacMan
      pacManAfterTick should not equal pacManAfterStart

      When("user changes the direction of the Pac-Man")
      val newDirectionRequest =
        NewDirectionRequest(newDirection = if (pacManAfterStart.direction == West) East else West)
      Put(s"/games/$gameId/direction", newDirectionRequest) ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }

      And("second tick duration passes")
      scheduler.tick(tickDuration)

      Then("Pac-Man direction changes")
      val pacManAfterSecondTick: PacMan =
        (Get(s"/games/$gameId") ~> route ~> check {
          responseAs[PacManStateResponse]
        }).pacMan

      pacManAfterSecondTick.direction should be(newDirectionRequest.newDirection)
    }
  }
}
