name := "pacman-multiplayer-fp"

organization := "miciek"

version := "1.0"

scalaVersion := "2.12.3"

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)


libraryDependencies ++= {
  val akkaHttpV = "10.0.10"
  val circeV = "0.8.0"
  val akkaHttpCirceV = "1.18.0"
  val catsV = "1.0.0-RC1"
  val monocleV = "1.4.0"
  val scalaTestV = "3.0.1"
  Seq(
    "io.circe" %% "circe-generic" % circeV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.github.julien-truffaut" %%  "monocle-core"  % monocleV,
    "com.github.julien-truffaut" %%  "monocle-macro" % monocleV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
    "org.scalatest" %% "scalatest" % scalaTestV % Test
  )
}

fork in run := true
