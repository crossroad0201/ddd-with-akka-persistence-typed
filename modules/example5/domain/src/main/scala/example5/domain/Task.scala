package example5.domain

import example5.domain.Task.{
  BackToTodoError,
  BackToTodoErrorByStillNotDone$,
  EditSubjectError,
  EditSubjectErrorByAlreadyDone,
  ToDoneError,
  ToDoneErrorByAlreadyDone
}
import example5.domain.TaskEvent.{ BackedToTodo, Created, Done, SubjectEdited }

case class Task(
    id: TaskId,
    subject: Subject,
    status: Status
) {
  def editSubject(
      newSubject: Subject
  ): Either[EditSubjectError, SubjectEdited] =
    if (status == Status.Todo)
      Right(SubjectEdited(newSubject))
    else
      Left(EditSubjectErrorByAlreadyDone)

  def toDone: Either[ToDoneError, Done.type] =
    if (status == Status.Todo)
      Right(Done)
    else
      Left(ToDoneErrorByAlreadyDone)

  def backToTodo: Either[BackToTodoError, BackedToTodo.type] =
    if (status == Status.Done)
      Right(BackedToTodo)
    else
      Left(BackToTodoErrorByStillNotDone$)

  def applyEvent(event: TaskMutationEvent): Task =
    event match {
      case SubjectEdited(newSubject) => copy(subject = newSubject)
      case event: Done.type => copy(status = event.status)
      case event: BackedToTodo.type => copy(status = event.status)
    }
}
object Task {
  def create(id: TaskId, subject: Subject): Created =
    Created(id, subject, Status.Todo)

  def applyEvent(event: TaskCreationEvent): Task =
    event match {
      case Created(id, subject, status) => Task(id, subject, status)
    }

  sealed trait EditSubjectError
  case object EditSubjectErrorByAlreadyDone extends EditSubjectError

  sealed trait ToDoneError
  case object ToDoneErrorByAlreadyDone extends ToDoneError

  sealed trait BackToTodoError
  case object BackToTodoErrorByStillNotDone$ extends BackToTodoError
}

case class TaskId(value: String)

case class Subject(value: String)

sealed abstract class Status(val value: String)
object Status {
  case object Todo extends Status("TODO")
  case object Done extends Status("DONE")
}
