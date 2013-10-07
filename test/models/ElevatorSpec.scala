package models

import org.specs2.mutable._
import org.specs2.specification.Scope
import scala.collection._

class ElevatorSpec extends Specification {

  trait complexContext extends Scope {
    var elevator = new Elevator(name="Cabine",floors = SortedSet(-2 to 0: _*) ++ SortedSet(10 to 15: _*))
  }

  "Elevator" should {
    "find its default floor" in new complexContext {
      elevator.defaultFloor must be equalTo 0
    }
    "can reverse direction" in new complexContext {
      elevator.reverse.currentDirection must be equalTo Direction.DOWN
      elevator.reverse.currentDirection must be equalTo Direction.UP
    }
    "open and shut doors" in new complexContext {
      elevator.open must be equalTo Command.OPEN
      elevator.isOpened must be equalTo true
      elevator.open must be equalTo Command.NOTHING
      elevator.close must be equalTo Command.CLOSE
      elevator.isOpened must be equalTo false
      elevator.close must be equalTo Command.NOTHING
    }
    "register calls " in new complexContext {
      elevator.call(0,Direction.UP)
      val callList = elevator.calls.getOrElse(0,Nil)
      callList.size must be equalTo 1
      callList match {
        case call :: tail => {
          call.direction must be equalTo Direction.UP
          call.floors must be equalTo Set(10,11,12,13,14,15)
        }
        case Nil => callList must have size 1
      }
    }
    "allow passengers to enter" in new complexContext {
      elevator.call(0,Direction.UP)
      elevator.currentFloor = 0
      elevator.enter(1)
      elevator.calls.getOrElse(0,Nil) must be equalTo Nil
      elevator.destinations must have size 0
    }
    "allow passengers to request destinations" in new complexContext {
      elevator.request(10)
      elevator.destinations.getOrElse(10,0) must be equalTo 1
    }
    "go up from floor to floor" in new complexContext {
      elevator.goNextFloor
      elevator.currentFloor must be equalTo 10
    }
    "allow passengers to exit" in new complexContext {
      elevator.call(0,Direction.UP)
      elevator.enter(1)
      elevator.request(10)
      elevator.currentFloor = 10
      elevator.exit(1)
      elevator.destinations.get(10) must be equalTo Some(0)
    }
    "go down from floor to floor" in new complexContext {
      elevator.reverse
      elevator.goNextFloor
      elevator.goNextFloor
      elevator.currentFloor must be equalTo -2
    }
    "stop at the top or bottom floor" in new complexContext {
      elevator.currentFloor = 15
      elevator.go(Some(Direction.UP)) must be equalTo Command.NOTHING
      elevator.currentFloor = -2
      elevator.go(Some(Direction.DOWN)) must be equalTo Command.NOTHING
    }
  }

  "Elevator controller" should {

    "open doors when idle at default floor" in new complexContext {
      elevator.command must be equalTo Command.OPEN
    }
    "go to an outstanding caller in correct direction" in new complexContext {
      elevator.call(10,Direction.UP)
      elevator.command must be equalTo Command.UP
    }
    "accept passengers" in new complexContext {
      "open doors to let someone enter" in {
        elevator.call(10,Direction.UP)
        elevator.command must be equalTo Command.CLOSE
        elevator.command must be equalTo Command.UP
        elevator.command must be equalTo Command.OPEN
        elevator.enter(1)
        elevator.call(10,Direction.DOWN)
        elevator.enter(1)
        elevator.request(11)
        elevator.request(0)
        elevator.command must be equalTo Command.CLOSE
      }
      "should prefer to continue in current direction" in {
        elevator.command must be equalTo Command.UP
        elevator.command must be equalTo Command.OPEN
        elevator.exit(1)
        elevator.command must be equalTo Command.CLOSE
      }
      "should know when to revert direction" in {
        elevator.command must be equalTo Command.DOWN
        elevator.command must be equalTo Command.DOWN
        elevator.command must be equalTo Command.OPEN
        elevator.exit(1)
        elevator.command must be equalTo Command.NOTHING
      }
    }
  }
}
