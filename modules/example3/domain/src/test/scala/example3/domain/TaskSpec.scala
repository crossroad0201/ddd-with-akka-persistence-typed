package example3.domain

import example3.domain.Task.{ BackToTodoErrorByStillNotDone$, EditSubjectErrorByAlreadyDone, ToDoneErrorByAlreadyDone }
import example3.domain.TaskEvent.{ BackedToTodo, Created, Done, SubjectEdited }
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

class TaskSpec extends AnyFreeSpec with Diagrams {

  private implicit def rightValue[R](value: Either[_, R]): R =
    value.getOrElse(throw new IllegalStateException(value.toString))

  "create" - {
    "Should be create a new task with initial status." in {
      val actualEvent = Task.create(TaskId("1"), Subject("Test"))
      assert(actualEvent == Created(TaskId("1"), Subject("Test"), Status.Todo))

      val actualState = Task.applyEvent(actualEvent)
      assert(actualState == Task(TaskId("1"), Subject("Test"), Status.Todo))
    }
  }

  "editSubject" - {
    "Should can modify subject if status is todo." in {
      val sut = Task.applyEvent(
        Task.create(
          TaskId("1"),
          Subject("Test")
        )
      )
      assert(sut.status == Status.Todo)

      val actualEvent = sut.editSubject(
        Subject("Edited")
      )

      assert(actualEvent == Right(SubjectEdited(Subject("Edited"))))

      val actualState = sut.applyEvent(actualEvent)
      assert(actualState == Task(TaskId("1"), Subject("Edited"), Status.Todo))
    }

    "Should be can not it if status is done." in {
      val sut: Task =
        for {
          task <- Right(Task.applyEvent(Task.create(TaskId("1"), Subject("Test"))))
          done <- task.toDone
        } yield task.applyEvent(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.editSubject(Subject("Edited"))
      assert(actualEvent == Left(EditSubjectErrorByAlreadyDone))
    }
  }

  "toDone" - {
    "Should can change status to done if status is todo." in {
      val sut = Task.applyEvent(Task.create(TaskId("1"), Subject("Test")))
      assert(sut.status == Status.Todo)

      val actualEvent = sut.toDone
      assert(actualEvent == Right(Done))

      val actualState = sut.applyEvent(actualEvent)
      assert(actualState == Task(TaskId("1"), Subject("Test"), Status.Done))
    }

    "Should be can not it if status is done." in {
      val sut: Task =
        for {
          task <- Right(Task.applyEvent(Task.create(TaskId("1"), Subject("Test"))))
          done <- task.toDone
        } yield task.applyEvent(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.toDone
      assert(actualEvent == Left(ToDoneErrorByAlreadyDone))
    }
  }

  "backToTodo" - {
    "Should can change status to todo if status is done." in {
      val sut: Task =
        for {
          task <- Right(Task.applyEvent(Task.create(TaskId("1"), Subject("Test"))))
          done <- task.toDone
        } yield task.applyEvent(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.backToTodo
      assert(actualEvent == Right(BackedToTodo))

      val actualState = sut.applyEvent(actualEvent)
      assert(actualState == Task(TaskId("1"), Subject("Test"), Status.Todo))
    }

    "Should be can not it if status is todo." in {
      val sut = Task.applyEvent(Task.create(TaskId("1"), Subject("Test")))
      assert(sut.status == Status.Todo)

      val actualEvent = sut.backToTodo
      assert(actualEvent == Left(BackToTodoErrorByStillNotDone$))
    }
  }

}
