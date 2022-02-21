package example5.domain

import example5.domain.Task._
import example5.domain.TaskEvent.{ BackedToTodo, Created, Done, SubjectEdited }

case class Task(
    id: TaskId,
    subject: Subject,
    status: Status
) {
  def editSubject(
      newSubject: Subject
  ): Either[EditSubjectError, Result[SubjectEdited, Task]] =
    if (status == Status.Todo) {
      Right(Result(SubjectEdited(newSubject), Task.this))
    } else
      Left(EditSubjectErrorByAlreadyDone)

  def toDone: Either[ToDoneError, Result[Done.type, Task]] =
    if (status == Status.Todo)
      Right(Result(Done, Task.this))
    else
      Left(ToDoneErrorByAlreadyDone)

  def backToTodo: Either[BackToTodoError, Result[BackedToTodo.type, Task]] =
    if (status == Status.Done)
      Right(Result(BackedToTodo, Task.this))
    else
      Left(BackToTodoErrorByStillNotDone)

}
object Task {
  def create(id: TaskId, subject: Subject): Result[Created, Task] =
    Result(Created(id, subject, Status.Todo))

  sealed trait TaskError

  sealed trait EditSubjectError extends TaskError
  case object EditSubjectErrorByAlreadyDone extends EditSubjectError

  sealed trait ToDoneError extends TaskError
  case object ToDoneErrorByAlreadyDone extends ToDoneError

  sealed trait BackToTodoError extends TaskError
  case object BackToTodoErrorByStillNotDone extends BackToTodoError
}

case class TaskId(value: String)

case class Subject(value: String)

sealed abstract class Status(val value: String)
object Status {
  case object Todo extends Status("TODO")
  case object Done extends Status("DONE")
}
