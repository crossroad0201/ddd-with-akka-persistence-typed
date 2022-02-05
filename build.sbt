import Dependencies._

lazy val akkaVersion = "2.6.18"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/

lazy val baseSettings = Seq(
  version := "1.0",
  scalaVersion := "2.13.1",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-encoding",
    "UTF-8",
    "-language:_",
    "-Yrangepos",
    "-Ywarn-unused",
    "-Xfatal-warnings"
  ),
  fork := true,
  libraryDependencies ++= Seq(
    ScalaTest.scalaTest % Test
  ),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val example1Domain =
  (project in file("modules/example1/domain"))
  .settings(baseSettings)

lazy val example1InterfaceAdapter =
  (project in file("modules/example1/interfaceAdapter"))
    .settings(baseSettings)
    .settings(
      libraryDependencies ++= Seq(
        Akka.Typed.actor,
        Akka.Typed.persistence,
        Logback.classic,
        Akka.Typed.actorTestKit % Test,
        Akka.Typed.persistenceTestKit % Test
      )
    )
    .dependsOn(example1Domain)

lazy val example2Domain =
  (project in file("modules/example2/domain"))
  .settings(baseSettings)

lazy val example2InterfaceAdapter =
  (project in file("modules/example2/interfaceAdapter"))
    .settings(baseSettings)
    .settings(
      libraryDependencies ++= Seq(
        Akka.Typed.actor,
        Akka.Typed.persistence,
        Logback.classic,
        Akka.Typed.actorTestKit % Test,
        Akka.Typed.persistenceTestKit % Test
      )
    )
    .dependsOn(example2Domain)

lazy val root = (project in file("."))
  .aggregate(
    example1Domain, example1InterfaceAdapter,
    example2Domain, example2InterfaceAdapter
  )
  .settings(baseSettings)
  .settings(
    name := "ddd-with-akka-persistence-typed",
    publishArtifact := false
  )
