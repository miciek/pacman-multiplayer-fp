lazy val root = (project in file("."))
  .settings(
    name := "pacman-multiplayer-fp",
    organization := "miciek",
    version := "1.0",
    scalaVersion := "2.12.7",
    scalacOptions ++= List(
      "-unchecked",
      "-Ywarn-unused-import",
      "-Xfatal-warnings",
      "-Ypartial-unification",
      "-language:higherKinds",
      "-Xlint"
    ),
    libraryDependencies ++= {
      val akkaHttpV      = "10.1.1"
      val circeV         = "0.9.3"
      val akkaHttpCirceV = "1.20.1"
      val monixV         = "2.3.3"
      val monocleV       = "1.5.0-cats"
      val scalaTestV     = "3.0.5"
      Seq(
        "com.typesafe.akka"          %% "akka-http-core"       % akkaHttpV,
        "com.typesafe.akka"          %% "akka-http-spray-json" % akkaHttpV,
        "com.github.julien-truffaut" %% "monocle-core"         % monocleV,
        "com.github.julien-truffaut" %% "monocle-macro"        % monocleV,
        "io.monix"                   %% "monix-execution"      % monixV,
        "io.circe"                   %% "circe-generic"        % circeV,
        "de.heikoseeberger"          %% "akka-http-circe"      % akkaHttpCirceV,
        "org.scalatest"              %% "scalatest"            % scalaTestV % Test,
        "com.typesafe.akka"          %% "akka-http-testkit"    % akkaHttpV % Test
      )
    },
    mainClass in assembly := Some("com.michalplachta.pacman.Main"),
    scalafmtOnCompile := true,
    addCommandAlias("formatAll", ";sbt:scalafmt;test:scalafmt;compile:scalafmt"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
  )
