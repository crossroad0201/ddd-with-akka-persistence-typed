package example1.interfaceadapter

import akka.actor.typed.Behavior
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{ Effect, EventSourcedBehavior, ReplyEffect }
import example1.domain._
import example1.interfaceadapter.TaskEvent._
import example1.interfaceadapter.TaskProtocol._

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

  def commandHandler(state: State, command: Command): ReplyEffect[TaskEvent, State] =
    (state, command) match {
      case (Empty(_), Create(subject, replyTo)) =>
        // NOTE Responsibility of create a event is on the persistent actor.
        Effect
          .persist(Created(subject))
          .thenReply(replyTo)(_ => CreateSucceeded)

      case (Empty(_), cmd: RequireCreated) =>
        Effect
          .reply(cmd.replyTo)(FailedByDoesNotExists)

      case (Just(_), Create(_, replyTo)) =>
        Effect
          .reply(replyTo)(CreateFailedByAlreadyExists)

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

      case (Just(entity), BackToTodo(replyTo)) =>
        if (entity.canBackToTodo)
          Effect
            .persist(BackedToTodo)
            .thenReply(replyTo)(_ => BackToTodoSucceeded$)
        else
          Effect
            .reply(replyTo)(BackToTodoFailedByStillNotDone$)

      case _ =>
        Effect.unhandled.thenNoReply
    }

  def eventHandler(state: State, event: TaskEvent): State =
    (state, event) match {
      case (Empty(id), Created(subject, state)) =>
        Just(Task.create(id, subject, state))
      case (Just(entity), SubjectEdited(newSubject)) =>
        Just(entity.editSubject(newSubject))
      case (Just(entity), Done) =>
        Just(entity.toDone)
      case (Just(entity), BackedToTodo) =>
        Just(entity.backToTodo)
      case _ =>
        throw new IllegalArgumentException()
    }

}
