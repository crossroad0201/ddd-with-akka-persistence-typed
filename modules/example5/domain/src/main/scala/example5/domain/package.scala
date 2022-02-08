package example5

package object domain {

  // NOTE There are 2 types of event.
  // One is a entity created event, that have play function for create new entity.
  // Another one is a entity updated event, that have playTo function for mutate exists entity.
  trait EntityCreationEvent[ENTITY] {
    def play: ENTITY
  }
  trait EntityMutationEvent[ENTITY] {
    def playTo(entity: ENTITY): ENTITY
  }

  // NOTE Result have event, and function for evaluate new state.
  // The entity function do not evaluate from persistent actor.
  trait Result[EVENT, ENTITY] {
    val event: EVENT
    def entity: ENTITY
  }
  object Result {
    def apply[EVENT <: EntityCreationEvent[ENTITY], ENTITY](
        event: EVENT
    ): Result[EVENT, ENTITY] = {
      val aEvent = event
      new Result[EVENT, ENTITY] {
        override val event       = aEvent
        override lazy val entity = event.play
      }
    }

    def apply[EVENT <: EntityMutationEvent[ENTITY], ENTITY](
        event: EVENT,
        entity: ENTITY
    ): Result[EVENT, ENTITY] = {
      val aEvent  = event
      val aEntity = entity
      new Result[EVENT, ENTITY] {
        override val event       = aEvent
        override lazy val entity = event.playTo(aEntity)
      }
    }
  }

}
