package example4

package object domain {

  // NOTE Events have play method for create entity or update state.
  trait EntityCreationEvent[ENTITY] {
    def play: ENTITY
  }
  trait EntityMutationEvent[ENTITY] {
    def playTo(entity: ENTITY): ENTITY
  }

  // NOTE Result have event, and function that return new updated state.
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
