package example3.domain

// NOTE Define the events in domain layer.
sealed trait TaskEvent

// NOTE Events have play method for create entity or update state.
trait TaskCreationEvent {
  def play: Task
}
trait TaskMutationEvent {
  def playTo(entity: Task): Task
}

object TaskEvent {

  case class Created(
      id: TaskId,
      subject: Subject,
      status: Status
  ) extends TaskEvent
      with TaskCreationEvent {
    override def play: Task =
      Task(id, subject, status)
  }

  case class SubjectEdited(
      newSubject: Subject
  ) extends TaskEvent
      with TaskMutationEvent {
    override def playTo(entity: Task): Task =
      entity.copy(subject = newSubject)
  }

  case object Done extends TaskEvent with TaskMutationEvent {
    val status = Status.Done
    override def playTo(entity: Task): Task =
      entity.copy(status = status)
  }

  case object BackedToTodo extends TaskEvent with TaskMutationEvent {
    val status = Status.Todo
    override def playTo(entity: Task): Task =
      entity.copy(status = status)
  }

}
