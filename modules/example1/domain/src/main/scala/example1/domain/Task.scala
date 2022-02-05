package example1.domain

case class Task(
    id: TaskId,
    subject: Subject,
    status: Status
) {
  // NOTE The domain object must define 2 methods for a behavior.
  // One is check the behavior is executable.
  // Another one is execute the behavior and return new state.
  def canEditSubject: Boolean =
    status != Status.Done

  def editSubject(newSubject: Subject): Task = {
    require(canEditSubject)
    copy(subject = newSubject)
  }

  def canToDone: Boolean =
    status != Status.Done

  def toDone: Task = {
    require(canToDone)
    copy(status = Status.Done)
  }

  def canBackToTodo: Boolean =
    status != Status.Todo

  def backToTodo: Task = {
    require(canBackToTodo)
    copy(status = Status.Todo)
  }
}
object Task {
  def create(id: TaskId, subject: Subject, status: Status = Status.Todo): Task =
    Task(id, subject, status)
}

case class TaskId(value: String)

case class Subject(value: String)

sealed abstract class Status(val value: String)
object Status {
  case object Todo extends Status("TODO")
  case object Done extends Status("DONE")
}
