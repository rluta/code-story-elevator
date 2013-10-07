package controllers

import play.api.mvc._
import models._
import scala.collection.SortedSet

object Application extends Controller {

  val floors = SortedSet(0 to 5: _*)
  val elevator = new Elevator(name = "Cabine", floors= floors)

  def call(atFloor: Int, goTo: String) = Action {
    try {
      val direction = Direction.byName(goTo)
      elevator.synchronized {
        elevator.call(atFloor,direction)
      }
      Ok("OK")
    } catch {
      case e:IllegalArgumentException => BadRequest(e.getMessage)
    }
  }

  def go(floorToGo: Int) = Action {
    if (!floors.exists(_ == floorToGo))
      BadRequest("Invalid floor number")
    else {
      elevator.synchronized {
        elevator.request(floorToGo)
      }
      Ok("OK")
    }
  }

  def userHasEntered = Action {
    elevator.synchronized {
      elevator.enter(1)
    }
    Ok("OK")
  }

  def userHasExited = Action {
    elevator.synchronized {
      elevator.exit(1)
    }
    Ok("OK")
  }

  def reset(cause: String) = Action {
    elevator.synchronized {
      elevator.reset()
    }
    Ok("OK")
  }

  def nextCommand = Action {
    elevator.synchronized {
      Ok(elevator.command.toString)
    }
  }

  def state = Action {
    elevator.synchronized {
      Ok(elevator.toString)
    }
  }
}