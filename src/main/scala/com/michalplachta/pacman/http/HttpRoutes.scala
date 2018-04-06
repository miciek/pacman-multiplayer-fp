package com.michalplachta.pacman.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.michalplachta.pacman.game.data.Direction
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import DirectionAsJson._

object HttpRoutes extends Directives {
  def setDirectionRoute[G](getGame: Int => Option[G],
                           setGame: (Int, G) => Unit,
                           setDirection: Direction => G => G): Route =
    path("games" / IntNumber / "direction") { gameId =>
      put {
        entity(as[NewDirectionRequest]) { request =>
          val maybeGame = getGame(gameId)
          val maybeUpdatedGame =
            maybeGame.map(setDirection(request.newDirection))
          maybeUpdatedGame match {
            case Some(updatedGame) =>
              setGame(gameId, updatedGame)
              complete(StatusCodes.OK)
            case _ =>
              complete(StatusCodes.NotFound)
          }
        }
      }
    }
}
