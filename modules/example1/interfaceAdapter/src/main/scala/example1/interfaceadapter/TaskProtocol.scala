package example1.interfaceadapter

import akka.actor.typed.ActorRef
import example1.domain.Subject

object TaskProtocol {

  sealed trait Command
  sealed trait Reply

  case object FailedByDoesNotExists extends Reply with EditSubjectReply with ToDoneReply with ReturnToTodoReply
  sealed trait RequireCreated {
    val replyTo: ActorRef[FailedByDoesNotExists.type]
  }

  case class Create(
      subject: Subject,
      replyTo: ActorRef[CreateReply]
  ) extends Command
  sealed trait CreateReply extends Reply
  case object CreateSucceeded extends CreateReply
  case object CreateFailedByAlreadyExists extends CreateReply

  case class EditSubject(
      newSubject: Subject,
      replyTo: ActorRef[EditSubjectReply]
  ) extends Command
      with RequireCreated
  sealed trait EditSubjectReply extends Reply
  case object EditSubjectSucceeded extends EditSubjectReply
  case object EditSubjectFailedByAlreadyDone extends EditSubjectReply

  case class ToDone(
      replyTo: ActorRef[ToDoneReply]
  ) extends Command
      with RequireCreated
  sealed trait ToDoneReply extends Reply
  case object ToDoneSucceeded extends ToDoneReply
  case object ToDoneFailedByAlreadyDone extends ToDoneReply

  case class ReturnToTodo(
      replyTo: ActorRef[ReturnToTodoReply]
  ) extends Command
      with RequireCreated
  sealed trait ReturnToTodoReply extends Reply
  case object ReturnToTodoSucceeded extends ReturnToTodoReply
  case object ReturnToTodoFailedByStillNotDone extends ReturnToTodoReply

}
