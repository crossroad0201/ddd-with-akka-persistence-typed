package example2.domain

// NOTE Define the events in domain layer.
sealed trait TaskEvent

object TaskEvent {

  case class Created(
      id: TaskId,
      subject: Subject,
      status: Status
  ) extends TaskEvent

  case class SubjectEdited(
      newSubject: Subject
  ) extends TaskEvent

  case object Done extends TaskEvent {
    val status = Status.Done
  }

  case object ReturnedToTodo extends TaskEvent {
    val status = Status.Todo
  }

}
