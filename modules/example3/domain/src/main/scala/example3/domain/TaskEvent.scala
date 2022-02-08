package example3.domain

// NOTE Define the events in domain layer.
sealed trait TaskEvent

sealed trait TaskCreationEvent extends TaskEvent {
  val id: TaskId
}
sealed trait TaskMutationEvent extends TaskEvent

object TaskEvent {

  case class Created(
      id: TaskId,
      subject: Subject,
      status: Status
  ) extends TaskCreationEvent

  case class SubjectEdited(
      newSubject: Subject
  ) extends TaskMutationEvent

  case object Done extends TaskMutationEvent {
    val status = Status.Done
  }

  case object BackedToTodo extends TaskMutationEvent {
    val status = Status.Todo
  }

}
