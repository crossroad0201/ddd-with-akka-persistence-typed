package example1.domain

sealed trait TaskEvent {
  val id: TaskId
}

case class TaskCreated(
    id: TaskId,
    subject: Subject
) extends TaskEvent

case class TaskSubjectEdited(
    id: TaskId,
    subject: Subject
) extends TaskEvent

case class TaskDone(
    id: TaskId
) extends TaskEvent

case class TaskReturnedToTodo(
    id: TaskId
) extends TaskEvent
