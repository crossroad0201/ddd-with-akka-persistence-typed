package example1.interfaceadapter

import example1.domain.Subject

// NOTE Define the events in interface adapter layer. (not domain layer)
sealed trait TaskEvent

object TaskEvent {

  case class Created(
      subject: Subject
  ) extends TaskEvent

  case class SubjectEdited(
      newSubject: Subject
  ) extends TaskEvent

  case object Done extends TaskEvent

  case object ReturnedToTodo extends TaskEvent

}
