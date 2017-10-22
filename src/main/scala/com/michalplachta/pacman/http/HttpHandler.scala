package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, HttpApp, Route}
import com.michalplachta.pacman.game.data.{Grid, PacMan}

class HttpHandler[S](initialState: S,
                     startNewGame: S => (S, Int),
                     getCurrentStep: (S, Int) => Option[Int],
                     getPacMan: (S, Int) => Option[PacMan]
                    ) extends HttpApp with GridJson {
  private var state: S = initialState

  val route: Route =
    path("grids" / "simpleSmall") {
      complete(Grid.simpleSmall)
    } ~
    path("games") {
      post {
        entity(as[StartGameRequest]) { request =>
          if (request.gridName == "simpleSmall") {
            val (newServerState, gameId) = startNewGame(state)
            state = newServerState
            complete(StartGameResponse(gameId))
          } else {
            complete((StatusCodes.NotFound, s"Grid with the name '${request.gridName}' couldn't be found"))
          }
        }
      }
    } ~
    path("games" / IntNumber) { gameId =>
      pacManStateResponse(gameId) { pacManStateResponse =>
        get {
          complete(pacManStateResponse)
        } ~
        put {
          entity(as[NewDirectionRequest]) { _ =>
            complete(StatusCodes.OK)
          }
        }
      } ~
      complete((StatusCodes.NotFound, s"Game with the id $gameId couldn't be found"))
    }

  protected def routes: Route = route

  private def pacManStateResponse(gameId: Int): Directive1[PacManStateResponse] = {
    val maybeResponse = for {
      currentStep <- getCurrentStep(state, gameId)
      pacMan <- getPacMan(state, gameId)
    } yield PacManStateResponse(currentStep, pacMan)
    maybeResponse.map(provide).getOrElse(reject)
  }
}

