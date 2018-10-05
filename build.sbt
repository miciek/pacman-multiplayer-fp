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
    libraryDependencies ++= Seq(
      "com.typesafe.akka"          %% "akka-http-core"       % "10.1.+",
      "com.typesafe.akka"          %% "akka-http-spray-json" % "10.1.+",
      "org.typelevel"              %% "cats-effect"          % "1.0.+",
      "com.github.julien-truffaut" %% "monocle-core"         % "1.5.0-cats",
      "com.github.julien-truffaut" %% "monocle-macro"        % "1.5.0-cats",
      "io.monix"                   %% "monix-execution"      % "2.3.+",
      "io.circe"                   %% "circe-generic"        % "0.9.+",
      "de.heikoseeberger"          %% "akka-http-circe"      % "1.20.+",
      "com.pepegar"                %% "hammock-core"         % "0.8.+",
      "com.pepegar"                %% "hammock-circe"        % "0.8.+",
      "org.scalatest"              %% "scalatest"            % "3.0.+" % Test,
      "com.typesafe.akka"          %% "akka-http-testkit"    % "10.1.+" % Test
    ),
    mainClass in assembly := Some("com.michalplachta.pacman.Main"),
    scalafmtOnCompile := true,
    addCommandAlias("formatAll", ";sbt:scalafmt;test:scalafmt;compile:scalafmt"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
  )
