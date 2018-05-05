name := "pacman-multiplayer-fp"

organization := "miciek"
version := "1.0"
scalaVersion := "2.12.6"

libraryDependencies ++= {
  val akkaHttpV = "10.1.1"
  val circeV = "0.9.3"
  val akkaHttpCirceV = "1.20.1"
  val monixV = "2.3.3"
  val monocleV = "1.5.0-cats"
  val scalaTestV = "3.0.5"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.github.julien-truffaut" %% "monocle-core" % monocleV,
    "com.github.julien-truffaut" %% "monocle-macro" % monocleV,
    "io.monix" %% "monix-execution" % monixV,
    "io.circe" %% "circe-generic" % circeV,
    "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-Ywarn-unused-import",
  "-Xfatal-warnings",
  "-Ypartial-unification",
  "-language:higherKinds",
  "-Xlint"
)

addCompilerPlugin(
  "org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")

scalafmtVersion in ThisBuild := "1.4.0"
scalafmtOnCompile in ThisBuild := true
