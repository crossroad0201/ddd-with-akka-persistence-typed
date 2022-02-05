package example2.domain

import example2.domain.Task.{ EditSubjectErrorByAlreadyDone, ReturnToTodoErrorByStillNotDone, ToDoneErrorByAlreadyDone }
import example2.domain.TaskEvent.{ Created, Done, ReturnedToTodo, SubjectEdited }
import org.scalatest.freespec.AnyFreeSpec

class TaskSpec extends AnyFreeSpec {

  private implicit def rightValue[R](value: Either[_, R]): R =
    value.getOrElse(throw new IllegalStateException(value.toString))

  "create" - {
    "Should be create a new task with initial status." in {
      val actualEvent = Task.create(
        TaskId("1"),
        Subject("Test")
      )

      assert(
        actualEvent == Created(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )

      val actualState = Task.applyCreated(actualEvent)

      assert(
        actualState == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )
    }
  }

  "editSubject" - {
    "Should can modify subject if status is todo." in {
      val sut = Task.applyCreated(
        Task.create(
          TaskId("1"),
          Subject("Test")
        )
      )
      assert(sut.status == Status.Todo)

      val actualEvent = sut.editSubject(
        Subject("Edited")
      )

      assert(
        actualEvent == Right(
          SubjectEdited(
            Subject("Edited")
          )
        )
      )

      val actualState = sut.applySubjectEdited(actualEvent)

      assert(
        actualState == Task(
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
            Task.applyCreated(
              Task.create(
                TaskId("1"),
                Subject("Test")
              )
            )
          )
          done <- task.toDone
        } yield task.applyDone(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.editSubject(
        Subject("Edited")
      )

      assert(actualEvent == Left(EditSubjectErrorByAlreadyDone))
    }
  }

  "toDone" - {
    "Should can change status to done if status is todo." in {
      val sut = Task.applyCreated(
        Task.create(
          TaskId("1"),
          Subject("Test")
        )
      )
      assert(sut.status == Status.Todo)

      val actualEvent = sut.toDone

      assert(actualEvent == Right(Done))

      val actualState = sut.applyDone(actualEvent)

      assert(
        actualState == Task(
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
            Task.applyCreated(
              Task.create(
                TaskId("1"),
                Subject("Test")
              )
            )
          )
          done <- task.toDone
        } yield task.applyDone(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.toDone

      assert(actualEvent == Left(ToDoneErrorByAlreadyDone))
    }
  }

  "returnToTodo" - {
    "Should can change status to todo if status is done." in {
      val sut: Task =
        for {
          task <- Right(
            Task.applyCreated(
              Task.create(
                TaskId("1"),
                Subject("Test")
              )
            )
          )
          done <- task.toDone
        } yield task.applyDone(done)
      assert(sut.status == Status.Done)

      val actualEvent = sut.returnToTodo

      assert(actualEvent == Right(ReturnedToTodo))

      val actualState = sut.applyReturnedToTodo(actualEvent)

      assert(
        actualState == Task(
          TaskId("1"),
          Subject("Test"),
          Status.Todo
        )
      )
    }

    "Should be can not it if status is todo." in {
      val sut = Task.applyCreated(
        Task.create(
          TaskId("1"),
          Subject("Test")
        )
      )
      assert(sut.status == Status.Todo)

      val actualEvent = sut.returnToTodo

      assert(actualEvent == Left(ReturnToTodoErrorByStillNotDone))
    }
  }

}