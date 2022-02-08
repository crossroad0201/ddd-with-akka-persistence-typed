Examples for implement domain object with Akka Persistence Typed
=====

# Premise

## Minimums about Akka Persistence Typed

### Steps when receiving a command

When Persistent Actor was received a command, processing it in the following steps.

1. **Check invariants** at current state.
2. **Execute domain logic** at current state.
3. **Create a event** with result of domain logic.(and persist it)
4. **Return a reply** to sender.(if necessary) 
5. **Update state** with the created event.

### Implementation limitations 

* Above 5 steps are needs implements into the 2 functions called [Command handler](https://doc.akka.io/docs/akka/current/typed/persistence.html#command-handler) 
and [Event handler](https://doc.akka.io/docs/akka/current/typed/persistence.html#event-handler) separately.
  * Step 1 to 4 implements in the Command handler.
  * Step 5 implements in the Event handler.

See official guide the [Akka Documentation / Persistence(Event Sourcing)](https://doc.akka.io/docs/akka/current/typed/persistence.html#introduction) for more information.

## Want to...

* Knowledge about the domain want to encapsulate in domain layer. (Domain logic, how to create events and how to update state...etc) 

* Domain objects do NOT want to be affected by specific technology(like Akka Persistence Typed...) as much as possible.    

* Domain logic is execute in the Command handler.
The Event handler only update the state by event, do not execute domain logic.

# The examples

## Overview

This repository contains 5 examples under the [modules/](./modules) directory.

There examples are about simple Task Management domains.

|Example|Summary|
|----|----|
|Example 1|Check invariants by delegating to domain objects.|
|Example 2|Domain objects has responsibilities create domain events and update state.|
|Example 3|Optimize update state functions.|
|Example 4|Domain events has responsibilities to update state.|
|Example 5|Domain objects returns event and state.|

### What contains?

Each examples are contains following sub-module and files.

|Sub module|Description|
|----|----|
|domain|Implements domain objects. This module do NOT depends to Akka Persistence Typed.|
|interfaceAdapter|Implements the Persistent Actor. This module depends to Akka Persistence Typed.|

|File|Description|
|----|----|
|Task.scala|The domain object. Entity and Value Objects.|
|TaskEvent.scala|The domain events. For example *TaskCreated*.|
|TaskSpec.scala|A test code for the domain object.|
|TaskPersistenceBehavior.scala|The Persistent Actor that implemented by Akka Persistence Typed.|
|TaskProtocol.scala|Message definitions for the Persistent Actor. Including some commands and replies.|
|TaskPersistenceBehaviorSpec.scala|A test code for the Persistent Actor.|

### What implements where?

| |Example 1|Example 2|Example 3|Example 4|Example 5|
|----|----|----|----|----|----|
|Which component to check invariants?|👍Domain objects|👍Domain objects|👍Domain objects|👍Domain objects|👍Domain objects|
|Which component to implement domain logic?|Persistent Actors|👍Domain objects|👍Domain objects|👍Domain objects|👍Domain objects|
|Where layer to define domain events?|interface adapter|👍domain|👍domain|👍domain|👍domain|
|Which component to create domain events?|Persistent Actors|👍Domain objects|👍Domain objects|👍Domain objects|👍Domain objects|
|Which component to update state?|👍Domain objects|👍Domain objects|👍Domain objects|👍Domain events|👍Domain events|

## Example 1

### Pros.

* Simplest code.

### Cons.

* Domain logic leaks to interface adapter layer.
* Knowledge for WHEN and HOW to create domain events leaks to interface adapter layer.
* Always needs define 2 methods per behavior. Check invariants and update state.
* Steps for get latest state of domain objects is complicated. (at Unit tests)

## Example 2

### Pros.

* **Domain logic is encapsulated in domain layer.**
* **Knowledge for WHEN/HOW to create domain events is encapsulated in domain objects.**

### Cons.

* Always needs define 2 methods per behavior. Create event after execute domain logic and update state.
* Steps for get latest state of domain objects is complicated. (at Unit tests)

## Example 3

### Pros.

* Domain logic is encapsulated in domain layer.
* Knowledge for WHEN/HOW to create domain events is encapsulated in domain objects.
* **Define method for update state per behavior is unnecessary.** 
* **Simplify eventHandler for Persistent Actor.**

### Cons.

* Steps for get latest state of domain objects is complicated. (at Unit tests)

## Example 4

### Pros.

* Domain logic is encapsulated in domain layer.
* Knowledge for WHEN/HOW to create domain events is encapsulated in domain objects.
* **Knowledge for HOW to update state is encapsulated in domain events.**
* Simplify eventHandler for Persistent Actor.

### Cons.

* Steps for get latest state of domain objects is complicated.  (at Unit tests)

## Example 5 

### Pros.

* Domain logic is encapsulated in domain layer.
* Knowledge for WHEN/HOW to create domain events is encapsulated in domain objects.
* Knowledge for HOW to update state is encapsulated in domain events.
* Simplify eventHandler for Persistent Actor.
* **Easily get latest state of domain objects. (at Unit tests)**

### Cons.

* Elaborate code (a little).

# Conclusion


I think so good idea for the Example 5.

But if you may feel too match, may be enough the Example 3 or 4.
