name := "pacman-multiplayer-fp"

organization := "miciek"

version := "1.0"

scalaVersion := "2.12.4"

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies ++= {
  val akkaHttpV = "10.0.10"
  val circeV = "0.8.0"
  val akkaHttpCirceV = "1.18.0"
  val monixV = "2.3.0"
  val monocleV = "1.4.0"
  val refinedV = "0.8.5"
  val scalaTestV = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.github.julien-truffaut" %%  "monocle-core"  % monocleV,
    "com.github.julien-truffaut" %%  "monocle-macro" % monocleV,
    "eu.timepit" %% "refined" % refinedV,
    "io.monix" %% "monix-execution" % monixV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-refined" % circeV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test
  )
}

fork in run := true
