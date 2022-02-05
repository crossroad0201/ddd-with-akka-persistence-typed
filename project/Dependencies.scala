import sbt._

object Dependencies {

  object Akka {
    private val version = "2.6.18"

    object Typed {
      val actor = "com.typesafe.akka" %% "akka-actor-typed" % version
      val actorTestKit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % version
    }
  }

  object Logback {
    private val version = "1.2.3"
    val classic = "ch.qos.logback" % "logback-classic" % version
  }

  object ScalaTest {
    private val version = "3.1.0"
    val scalaTest = "org.scalatest" %% "scalatest" % version
  }

}
