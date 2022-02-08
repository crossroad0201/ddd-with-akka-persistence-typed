package example5.domain

import example5.domain.Task.{ BackToTodoErrorByStillNotDone$, EditSubjectErrorByAlreadyDone, ToDoneErrorByAlreadyDone }
import example5.domain.TaskEvent.{ BackedToTodo, Created, Done, SubjectEdited }
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

class TaskSpec extends AnyFreeSpec with Diagrams {

  private implicit def rightValue[R](value: Either[_, R]): R =
    value.getOrElse(throw new IllegalStateException(value.toString))

  "create" - {
    "Should be create a new task with initial status." in {
      val actual = Task.create(
        TaskId("1"),
        Subject("Test")
      )

      assert(
        actual.event == Created(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )

      assert(
        actual.entity == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )
    }
  }

  "editSubject" - {
    "Should can modify subject if status is todo." in {
      val sut =
        Task
          .create(
            TaskId("1"),
            Subject("Test")
          )
          .entity
      assert(sut.status == Status.Todo)

      val actual = sut.editSubject(
        Subject("Edited")
      )

      assert(actual.isRight)
      assert(
        actual.event ==
          SubjectEdited(
            Subject("Edited")
          )
      )
      assert(
        actual.entity == Task(
          TaskId("1"),
          Subject("Edited"),
          Status.Todo
        )
      )
    }

    "Should be can not it if status is done." in {
      val sut: Task =
        for {
          task <- Right(
            Task
              .create(
                TaskId("1"),
                Subject("Test")
              )
              .entity
          )
          done <- task.toDone
        } yield done.entity
      assert(sut.status == Status.Done)

      val actual = sut.editSubject(
        Subject("Edited")
      )

      assert(actual == Left(EditSubjectErrorByAlreadyDone))
    }
  }

  "toDone" - {
    "Should can change status to done if status is todo." in {
      val sut =
        Task
          .create(
            TaskId("1"),
            Subject("Test")
          )
          .entity
      assert(sut.status == Status.Todo)

      val actual = sut.toDone

      assert(actual.isRight)
      assert(actual.event == Done)
      assert(
        actual.entity == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Done
        )
      )
    }

    "Should be can not it if status is done." in {
      val sut: Task =
        for {
          task <- Right(
            Task
              .create(
                TaskId("1"),
                Subject("Test")
              )
              .entity
          )
          done <- task.toDone
        } yield done.entity
      assert(sut.status == Status.Done)

      val actual = sut.toDone

      assert(actual == Left(ToDoneErrorByAlreadyDone))
    }
  }

  "backToTodo" - {
    "Should can change status to todo if status is done." in {
      val sut: Task =
        for {
          task <- Right(
            Task
              .create(
                TaskId("1"),
                Subject("Test")
              )
              .entity
          )
          done <- task.toDone
        } yield done.entity
      assert(sut.status == Status.Done)

      val actual = sut.backToTodo

      assert(actual.isRight)
      assert(actual.event == BackedToTodo)
      assert(
        actual.entity == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )
    }

    "Should be can not it if status is todo." in {
      val sut =
        Task
          .create(
            TaskId("1"),
            Subject("Test")
          )
          .entity
      assert(sut.status == Status.Todo)

      val actual = sut.backToTodo

      assert(actual == Left(BackToTodoErrorByStillNotDone$))
    }
  }

}
