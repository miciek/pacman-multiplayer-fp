name := "pacman-multiplayer-akka-streams"

organization := "miciek"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val akkaVersion = "2.5.6"
  val akkaHttpVersion = "10.0.10"
  val logbackVersion = "1.1.7"
  val scalaTestVersion = "3.0.1"
  val junitVersion = "4.12"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    // LOGGING
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    // TESTING
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "junit" % "junit" % junitVersion % Test
  )
}

fork in run := true

