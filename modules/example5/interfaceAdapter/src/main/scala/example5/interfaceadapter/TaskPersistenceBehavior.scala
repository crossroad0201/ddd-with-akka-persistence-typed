package example5.interfaceadapter

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior, ReplyEffect }
import example5.domain.Task.{ BackToTodoErrorByStillNotDone, EditSubjectErrorByAlreadyDone, ToDoneErrorByAlreadyDone }
import example5.domain._
import example5.interfaceadapter.TaskProtocol._

object TaskPersistenceBehavior {

  sealed trait State {
    def id: TaskId
  }
  case class Empty(id: TaskId) extends State
  case class Just(entity: Task) extends State {
    override def id = entity.id
  }

  def apply(
      id: TaskId,
      // NOTE Take optional parameter for initial state, that convenient for unit test.
      initialState: TaskId => State = id => Empty(id)
  ): Behavior[Command] =
    EventSourcedBehavior.withEnforcedReplies(
      PersistenceId.of("task", id.value),
      initialState(id),
      commandHandler,
      eventHandler
    )

  private def commandHandler(
      state: State,
      command: Command
  ): ReplyEffect[TaskEvent, State] =
    (state, command) match {
      case (Empty(id), Create(subject, replyTo)) =>
        Effect
          .persist(
            Task.create(id, subject).event
          )
          .thenReply(replyTo)(_ => CreateSucceeded)

      case (Empty(_), cmd: RequireCreated) =>
        Effect
          .reply(cmd.replyTo)(FailedByDoesNotExists)

      case (Just(_), Create(_, replyTo)) =>
        Effect
          .reply(replyTo)(CreateFailedByAlreadyExists)

      case (Just(entity), EditSubject(newSubject, replyTo)) =>
        entity.editSubject(newSubject) match {
          case Right(result) =>
            Effect
              .persist(result.event)
              .thenReply(replyTo)(_ => EditSubjectSucceeded)
          case Left(EditSubjectErrorByAlreadyDone) =>
            Effect
              .reply(replyTo)(EditSubjectFailedByAlreadyDone)
        }

      case (Just(entity), ToDone(replyTo)) =>
        entity.toDone match {
          case Right(result) =>
            Effect
              .persist(result.event)
              .thenReply(replyTo)(_ => ToDoneSucceeded)
          case Left(ToDoneErrorByAlreadyDone) =>
            Effect
              .reply(replyTo)(ToDoneFailedByAlreadyDone)
        }

      case (Just(entity), BackToTodo(replyTo)) =>
        entity.backToTodo match {
          case Right(result) =>
            Effect
              .persist(result.event)
              .thenReply(replyTo)(_ => BackToTodoSucceeded)
          case Left(BackToTodoErrorByStillNotDone) =>
            Effect
              .reply(replyTo)(BackToTodoFailedByStillNotDone)
        }

      case _ =>
        Effect.unhandled.thenNoReply
    }

  def eventHandler(state: State, event: TaskEvent): State =
    (state, event) match {
      case (Empty(_), event: TaskCreationEvent) =>
        Just(event.play)
      case (Just(entity), event: TaskMutationEvent) =>
        Just(event.playTo(entity))
      case _ =>
        throw new IllegalArgumentException()
    }

}
