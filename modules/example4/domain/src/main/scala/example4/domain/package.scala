package example4

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

}
