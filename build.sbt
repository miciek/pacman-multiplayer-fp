name := "pacman-multiplayer-akka-streams"

organization := "miciek"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val akkaV = "2.5.6"
  val akkaHttpV = "10.0.10"
  val scalaTestV = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
  )
}

fork in run := true
