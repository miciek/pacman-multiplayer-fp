package com.michalplachta.pacman.http
import cats.effect.IO
import com.michalplachta.pacman.game.data.Position
import hammock._
import io.circe.generic.auto._
import hammock.circe.implicits._
import hammock.jvm.Interpreter

class CollectiblesRequests(baseUri: String) {
  private implicit val interpreter = Interpreter[IO]

  def create(id: Int, positions: Set[Position], headers: Map[String, String]): IO[Unit] = {
    Hammock
      .request(Method.PUT, Uri.unsafeParse(s"$baseUri/$id"), headers, Some(positions))
      .as[Int]
      .exec[IO]
      .map(_ => ())
  }
}
