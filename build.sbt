import Dependencies._

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

lazy val example5Domain =
  (project in file("modules/example5/domain"))
  .settings(baseSettings)

lazy val example5InterfaceAdapter =
  (project in file("modules/example5/interfaceAdapter"))
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
    .dependsOn(example5Domain)

lazy val example3Domain =
  (project in file("modules/example3/domain"))
    .settings(baseSettings)

lazy val example3InterfaceAdapter =
  (project in file("modules/example3/interfaceAdapter"))
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
    .dependsOn(example3Domain)

lazy val example4Domain =
  (project in file("modules/example4/domain"))
    .settings(baseSettings)

lazy val example4InterfaceAdapter =
  (project in file("modules/example4/interfaceAdapter"))
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
    .dependsOn(example4Domain)

lazy val root = (project in file("."))
  .aggregate(
    example1Domain, example1InterfaceAdapter,
    example2Domain, example2InterfaceAdapter,
    example3Domain, example3InterfaceAdapter,
    example4Domain, example4InterfaceAdapter,
    example5Domain, example5InterfaceAdapter
  )
  .settings(baseSettings)
  .settings(
    name := "ddd-with-akka-persistence-typed",
    publishArtifact := false
  )
