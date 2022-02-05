package example4.domain

// NOTE Define the events in domain layer.
sealed trait TaskEvent

trait TaskCreationEvent extends TaskEvent with EntityCreationEvent[Task]
trait TaskMutationEvent extends TaskEvent with EntityMutationEvent[Task]

object TaskEvent {

  case class Created(
      id: TaskId,
      subject: Subject,
      status: Status
  ) extends TaskCreationEvent {
    override def play: Task =
      Task(id, subject, status)
  }

  case class SubjectEdited(
      newSubject: Subject
  ) extends TaskMutationEvent {
    override def playTo(entity: Task): Task =
      entity.copy(subject = newSubject)
  }

  case object Done extends TaskMutationEvent {
    val status = Status.Done
    override def playTo(entity: Task): Task =
      entity.copy(status = status)
  }

  case object BackedToTodo extends TaskMutationEvent {
    val status = Status.Todo
    override def playTo(entity: Task): Task =
      entity.copy(status = status)
  }

}
