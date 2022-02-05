package example2.interfaceadapter

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import com.typesafe.config.ConfigFactory
import example2.domain.TaskEvent.{ BackedToTodo, Created, Done, SubjectEdited }
import example2.domain._
import example2.interfaceadapter.TaskPersistenceBehavior.{ Empty, Just, State }
import example2.interfaceadapter.TaskProtocol._
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpecLike

class TaskPersistenceBehaviorSpec
    extends ScalaTestWithActorTestKit(
      EventSourcedBehaviorTestKit.config
        .withFallback(
          // NOTE Enable Java Serialization in this test.
          ConfigFactory.parseString(
            """
            |akka.actor {
            |  allow-java-serialization = yes
            |  warn-about-java-serializer-usage = false
            |}
            |""".stripMargin
          )
        )
    )
    with AnyFreeSpecLike
    with Diagrams {

  def createSutWithState(
      id: TaskId,
      initialState: TaskId => State
  ): EventSourcedBehaviorTestKit[Command, TaskEvent, State] =
    EventSourcedBehaviorTestKit(
      system,
      TaskPersistenceBehavior(id, initialState)
    )

  "Create" - {
    "In Empty state" - {
      "Should reply succeeded." in {
        val sut = createSutWithState(TaskId("1"), id => Empty(id))

        val actual = sut.runCommand[CreateReply] { replyTo =>
          Create(Subject("Test"), replyTo)
        }

        assert(actual.reply == CreateSucceeded)
        assert(
          actual.event == Created(
            TaskId("1"),
            Subject("Test"),
            Status.Todo
          )
        )
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Todo
            )
          )
        )
      }
    }

    "In Just state" - {
      "Should reply failed by already exists." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Todo
              )
            )
        )

        val actual = sut.runCommand[CreateReply] { replyTo =>
          Create(Subject("Test"), replyTo)
        }

        assert(actual.reply == CreateFailedByAlreadyExists)
        assert(actual.hasNoEvents)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Todo
            )
          )
        )
      }
    }
  }

  "EditSubject" - {
    "In Empty state" - {
      "Should reply failed by does not exists." in {
        val sut = createSutWithState(TaskId("1"), id => Empty(id))

        val actual = sut.runCommand[EditSubjectReply] { replyTo =>
          EditSubject(Subject("Edited"), replyTo)
        }

        assert(actual.reply == FailedByDoesNotExists)
        assert(actual.hasNoEvents)
        assert(actual.state == Empty(TaskId("1")))
      }
    }

    "In Just state" - {
      "Should reply succeeded if status is todo." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Todo
              )
            )
        )

        val actual = sut.runCommand[EditSubjectReply] { replyTo =>
          EditSubject(Subject("Edited"), replyTo)
        }

        assert(actual.reply == EditSubjectSucceeded)
        assert(
          actual.event ==
            SubjectEdited(Subject("Edited"))
        )
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Edited"),
              Status.Todo
            )
          )
        )
      }

      "Should reply failed by already done if status is done." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Done
              )
            )
        )

        val actual = sut.runCommand[EditSubjectReply] { replyTo =>
          EditSubject(Subject("Edited"), replyTo)
        }

        assert(actual.reply == EditSubjectFailedByAlreadyDone)
        assert(actual.hasNoEvents)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Done
            )
          )
        )
      }
    }
  }

  "ToDone" - {
    "In Empty state" - {
      "Should reply failed by does not exists." in {
        val sut = createSutWithState(TaskId("1"), id => Empty(id))

        val actual = sut.runCommand[ToDoneReply] { replyTo =>
          ToDone(replyTo)
        }

        assert(actual.reply == FailedByDoesNotExists)
        assert(actual.hasNoEvents)
        assert(actual.state == Empty(TaskId("1")))
      }
    }

    "In Just state" - {
      "Should reply succeeded if status is todo." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Todo
              )
            )
        )

        val actual = sut.runCommand[ToDoneReply] { replyTo =>
          ToDone(replyTo)
        }

        assert(actual.reply == ToDoneSucceeded)
        assert(actual.event == Done)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Done
            )
          )
        )
      }

      "Should reply failed by already done if status is done." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Done
              )
            )
        )

        val actual = sut.runCommand[ToDoneReply] { replyTo =>
          ToDone(replyTo)
        }

        assert(actual.reply == ToDoneFailedByAlreadyDone)
        assert(actual.hasNoEvents)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Done
            )
          )
        )
      }
    }
  }

  "BackToTodo" - {
    "In Empty state" - {
      "Should reply failed by does not exists." in {
        val sut = createSutWithState(TaskId("1"), id => Empty(id))

        val actual = sut.runCommand[BackToTodoReply] { replyTo =>
          BackToTodo(replyTo)
        }

        assert(actual.reply == FailedByDoesNotExists)
        assert(actual.hasNoEvents)
        assert(actual.state == Empty(TaskId("1")))
      }
    }

    "In Just state" - {
      "Should reply succeeded if status is done." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Done
              )
            )
        )

        val actual = sut.runCommand[BackToTodoReply] { replyTo =>
          BackToTodo(replyTo)
        }

        assert(actual.reply == BackToTodoSucceeded)
        assert(actual.event == BackedToTodo)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Todo
            )
          )
        )
      }

      "Should reply failed by already done if status is todo." in {
        val sut = createSutWithState(
          TaskId("1"),
          id =>
            Just(
              Task(
                id,
                Subject("Test"),
                Status.Todo
              )
            )
        )

        val actual = sut.runCommand[BackToTodoReply] { replyTo =>
          BackToTodo(replyTo)
        }

        assert(actual.reply == BackToTodoFailedByStillNotDone)
        assert(actual.hasNoEvents)
        assert(
          actual.state == Just(
            Task(
              TaskId("1"),
              Subject("Test"),
              Status.Todo
            )
          )
        )
      }
    }
  }

}
