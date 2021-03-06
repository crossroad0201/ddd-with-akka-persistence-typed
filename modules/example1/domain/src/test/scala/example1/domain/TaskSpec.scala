package example1.domain

import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpecLike

class TaskSpec extends AnyFreeSpecLike with Diagrams {

  "create" - {
    "Should be create a new task with initial status." in {
      val actual = Task.create(TaskId("1"), Subject("Test"))

      assert(actual == Task(TaskId("1"), Subject("Test"), Status.Todo))
    }
  }

  "editSubject" - {
    "Should can modify subject if status is todo." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test"))
      assert(sut.status == Status.Todo)

      val actualCheckResult = sut.canEditSubject
      assert(actualCheckResult)

      val actualEntity = sut.editSubject(Subject("Edited"))
      assert(actualEntity == Task(TaskId("1"), Subject("Edited"), Status.Todo))
    }

    "Should be can not it if status is done." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test")).toDone
      assert(sut.status == Status.Done)

      val actualCheckResult = sut.canEditSubject
      assert(!actualCheckResult)

      intercept[IllegalArgumentException] {
        sut.editSubject(Subject("Edited"))
      }
    }
  }

  "toDone" - {
    "Should can change status to done if status is todo." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test"))
      assert(sut.status == Status.Todo)

      val actualCheckResult = sut.canToDone
      assert(actualCheckResult)

      val actualEntity = sut.toDone
      assert(actualEntity == Task(TaskId("1"), Subject("Test"), Status.Done))
    }

    "Should be can not it if status is done." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test")).toDone
      assert(sut.status == Status.Done)

      val actualCheckResult = sut.canToDone
      assert(!actualCheckResult)

      intercept[IllegalArgumentException] {
        sut.toDone
      }
    }
  }

  "backToTodo" - {
    "Should can change status to todo if status is done." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test")).toDone
      assert(sut.status == Status.Done)

      val actualCheckResult = sut.canBackToTodo
      assert(actualCheckResult)

      val actualEntity = sut.backToTodo
      assert(actualEntity == Task(TaskId("1"), Subject("Test"), Status.Todo))
    }

    "Should be can not it if status is todo." in {
      val sut: Task = Task.create(TaskId("1"), Subject("Test"))
      assert(sut.status == Status.Todo)

      val actualCheckResult = sut.canBackToTodo
      assert(!actualCheckResult)

      intercept[IllegalArgumentException] {
        sut.backToTodo
      }
    }
  }

}
