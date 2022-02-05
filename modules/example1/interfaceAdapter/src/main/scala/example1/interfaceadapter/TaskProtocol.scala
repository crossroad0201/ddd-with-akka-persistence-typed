package example1.interfaceadapter

import akka.actor.typed.ActorRef
import example1.domain.Subject

object TaskProtocol {

  sealed trait Command
  sealed trait Reply

  case class Create(
      subject: Subject,
      replyTo: ActorRef[CreateReply]
  ) extends Command
  sealed trait CreateReply extends Reply
  case object CreateSucceeded extends CreateReply

  case class EditSubject(
      newSubject: Subject,
      replyTo: ActorRef[EditSubjectReply]
  ) extends Command
  sealed trait EditSubjectReply extends Reply
  case object EditSubjectSucceeded extends EditSubjectReply
  case object EditSubjectFailedByAlreadyDone extends EditSubjectReply

  case class ToDone(
      replyTo: ActorRef[ToDoneReply]
  ) extends Command
  sealed trait ToDoneReply extends Reply
  case object ToDoneSucceeded extends ToDoneReply
  case object ToDoneFailedByAlreadyDone extends ToDoneReply

  case class ReturnToTodo(
      replyTo: ActorRef[ReturnToTodoReply]
  ) extends Command
  sealed trait ReturnToTodoReply extends Reply
  case object ReturnToTodoSucceeded extends ReturnToTodoReply
  case object ReturnToTodoFailedByStillNotDone extends ReturnToTodoReply

}
