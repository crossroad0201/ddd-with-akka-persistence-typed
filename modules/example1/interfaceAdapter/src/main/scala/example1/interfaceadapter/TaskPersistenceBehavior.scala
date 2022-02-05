package example1.interfaceadapter

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior, ReplyEffect }
import example1.domain._
import example1.interfaceadapter.TaskEvent._
import example1.interfaceadapter.TaskProtocol._

object TaskPersistenceBehavior {

  sealed trait State
  private case class Empty(id: TaskId) extends State
  private case class Just(task: Task) extends State

  def apply(id: TaskId): Behavior[TaskProtocol.Command] =
    EventSourcedBehavior.withEnforcedReplies(
      PersistenceId.of("task", id.value),
      Empty(id),
      commandHandler,
      eventHandler
    )

  def commandHandler(state: State, command: TaskProtocol.Command): ReplyEffect[TaskEvent, State] =
    (state, command) match {
      case (Empty(_), Create(subject, replyTo)) =>
        // NOTE Responsibility of create a event is on the persistent actor.
        Effect
          .persist(Created(subject))
          .thenReply(replyTo)(_ => CreateSucceeded)

      case (Just(entity), EditSubject(subject, replyTo)) =>
        if (entity.canEditSubject)
          Effect
            .persist(SubjectEdited(subject))
            .thenReply(replyTo)(_ => EditSubjectSucceeded)
        else
          // NOTE The persistent actor must know why they can't execute commands, for example that was already done.
          Effect
            .reply(replyTo)(EditSubjectFailedByAlreadyDone)

      case (Just(entity), ToDone(replyTo)) =>
        if (entity.canToDone)
          Effect
            .persist(Done)
            .thenReply(replyTo)(_ => ToDoneSucceeded)
        else
          Effect
            .reply(replyTo)(ToDoneFailedByAlreadyDone)

      case (Just(entity), ReturnToTodo(replyTo)) =>
        if (entity.canReturnToTodo)
          Effect
            .persist(ReturnedToTodo)
            .thenReply(replyTo)(_ => ReturnToTodoSucceeded)
        else
          Effect
            .reply(replyTo)(ReturnToTodoFailedByStillNotDone)

      case _ =>
        Effect.unhandled.thenNoReply
    }

  def eventHandler(state: State, event: TaskEvent): State =
    (state, event) match {
      case (Empty(id), Created(subject)) =>
        Just(Task.create(id, subject))
      case (Just(entity), SubjectEdited(newSubject)) =>
        Just(entity.editSubject(newSubject))
      case (Just(entity), Done) =>
        Just(entity.toDone)
      case (Just(entity), ReturnedToTodo) =>
        Just(entity.returnToTodo)
      case _ =>
        throw new IllegalArgumentException()
    }

}
