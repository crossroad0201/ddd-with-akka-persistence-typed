package example1.domain

import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpecLike

class TaskSpec extends AnyFreeSpecLike with Diagrams {

  "create" - {
    "Should be create a new task with initial status." in {
      val actual = Task.create(
        TaskId("1"),
        Subject("Test")
      )

      assert(
        actual == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )
    }
  }

  "editSubject" - {
    "Should can modify subject if status is todo." in {
      val sut = Task.create(
        TaskId("1"),
        Subject("Test")
      )
      assert(sut.status == Status.Todo)

      {
        val actual = sut.canEditSubject
        assert(actual)
      }

      {
        val actual = sut.editSubject(Subject("Edited"))
        assert(
          actual == Task(
            TaskId("1"),
            Subject("Edited"),
            Status.Todo
          )
        )
      }
    }

    "Should be can not it if status is done." in {
      val sut = Task
        .create(
          TaskId("1"),
          Subject("Test")
        )
        .toDone
      assert(sut.status == Status.Done)

      {
        val actual = sut.canEditSubject
        assert(!actual)
      }

      {
        intercept[IllegalArgumentException] {
          sut.editSubject(Subject("Edited"))
        }
      }
    }
  }

  "toDone" - {
    "Should can change status to done if status is todo." in {
      val sut = Task
        .create(
          TaskId("1"),
          Subject("Test")
        )
      assert(sut.status == Status.Todo)

      {
        val actual = sut.canToDone
        assert(actual)
      }

      {
        val actual = sut.toDone
        assert(
          actual == Task(
            TaskId("1"),
            Subject("Test"),
            Status.Done
          )
        )
      }
    }

    "Should be can not it if status is done." in {
      val sut = Task
        .create(
          TaskId("1"),
          Subject("Test")
        )
        .toDone
      assert(sut.status == Status.Done)

      {
        val actual = sut.canToDone
        assert(!actual)
      }

      {
        intercept[IllegalArgumentException] {
          sut.toDone
        }
      }
    }
  }

  "returnToTodo" - {
    "Should can change status to todo if status is done." in {
      val sut = Task
        .create(
          TaskId("1"),
          Subject("Test")
        )
        .toDone
      assert(sut.status == Status.Done)

      {
        val actual = sut.canReturnToTodo
        assert(actual)
      }

      {
        val actual = sut.returnToTodo
        assert(
          actual == Task(
            TaskId("1"),
            Subject("Test"),
            Status.Todo
          )
        )
      }
    }

    "Should be can not it if status is todo." in {
      val sut = Task
        .create(
          TaskId("1"),
          Subject("Test")
        )
      assert(sut.status == Status.Todo)

      {
        val actual = sut.canReturnToTodo
        assert(!actual)
      }

      {
        intercept[IllegalArgumentException] {
          sut.returnToTodo
        }
      }
    }
  }

}
