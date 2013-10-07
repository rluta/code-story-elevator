package models

import scala.collection._
import akka.actor.{Props, Actor}

/**
  This class describes the data model for the elevator cabin
 */
object Elevator {
  case class Call(direction: Direction, floors: Set[Int])
}

class Elevator(name: String, floors: SortedSet[Int]) {

  lazy val defaultFloor = if (floors.exists(_ == 0)) 0 else floors.min

  var currentFloor:Int = defaultFloor
  var doorIsOpened = false
  var currentDirection = Direction.UP
  var calls:mutable.Map[Int,List[Elevator.Call]] = mutable.Map()
  var destinations:mutable.Map[Int,Int] = mutable.Map()

  def reset() = {
    currentFloor = defaultFloor
    doorIsOpened = false
    currentDirection = Direction.UP
    calls = mutable.Map()
    destinations = mutable.Map()
  }

  def call(atFloor: Int, direction: Direction) = {
    if (!floors.exists(_ == atFloor))
      throw new IllegalArgumentException("Unknown floor "+ atFloor)
    val dir = if (atFloor == floors.head || (direction == Direction.UP && atFloor != floors.last ))
      Direction.UP
    else
      Direction.DOWN
    val myCall = Elevator.Call(dir, floors.filter(dir.compareTo(atFloor)))

    calls.get(atFloor) match {
      case Some(callList) => calls.update(atFloor,myCall :: callList)
      case None => calls.update(atFloor,myCall :: Nil)
    }
  }

  def request(floor: Int) =
    destinations.update(floor,destinations.getOrElse(floor,0)+1)

  def enter(num: Int) =
    calls.get(currentFloor) match {
      case Some(callList) => calls.update(currentFloor,callList.drop(num))
      case None => throw new IllegalStateException("Can't find any calllers waiting at floor "+currentFloor)
    }

  def exit(num: Int) = {
    destinations.update(currentFloor,Math.max(0,destinations.getOrElse(currentFloor,0)-num))
  }

  def nextFloor(direction: Direction): Option[Int] = {
    direction match {
      case Direction.UP => floors.find(_ > currentFloor)
      case Direction.DOWN => floors.filter(_ < currentFloor).lastOption
    }
  }

  def selectDirection(toFloor: Int): Option[Direction] =
    if (currentFloor < toFloor)
      Some(Direction.UP)
    else if (currentFloor > toFloor)
      Some(Direction.DOWN)
    else
      None

  def goToDefaultFloor = goToFloor(defaultFloor)

  def goToFloor(floor:Int):Command = go(selectDirection(floor))

  def goNextFloor = go(Some(currentDirection))

  def go(directionOption:Option[Direction]):Command = directionOption match {
    case Some(direction) => {
      nextFloor(direction) match {
        case Some(floor) => {
          currentFloor = floor
          direction.command
        }
        case None => Command.NOTHING
      }
    }
    case None => Command.NOTHING
  }

  def reverse = {
    currentDirection = currentDirection.inverse
    this
  }

  def isOpened = doorIsOpened

  def open = if (doorIsOpened)
    Command.NOTHING
  else {
    doorIsOpened = true
    Command.OPEN
  }

  def close = if (!doorIsOpened)
    Command.NOTHING
  else {
    doorIsOpened = false
    Command.CLOSE
  }

  def command:Command = {
    // someone needs to exit at this floor ?
    val exitAtFloor = destinations.getOrElse(currentFloor,0) > 0
    // someone waiting to enter at this floor for the current direction ?
    val enterAtFloor = calls.getOrElse(currentFloor,Nil)
      .exists(x => x.direction == currentDirection || currentFloor == floors.head || currentFloor == floors.last)
    // are we waiting for new passengers at default floor ?
    val isIdle = currentFloor == defaultFloor && destinations.values.sum + calls.values.flatten.size == 0

    if (isOpened && !isIdle)
      close
    else if (enterAtFloor || exitAtFloor || isIdle)
      open
    else
      move
  }

  def move: Command =
  // Handle the degenerate case first, in a building with a single floor or less the elevator can't move
    if (floors.size <= 1)
      Command.NOTHING
    else {
      // Find the best direction
      val comparator = currentDirection.compareTo(currentFloor)
      val invComparator = currentDirection.inverse.compareTo(currentFloor)
      val shouldContinue =
         destinations.filterKeys(comparator).values.sum > 0 || calls.filterKeys(comparator).values.flatten.size > 0
      val shouldReverse =
        destinations.filterKeys(invComparator).values.sum > 0 ||
        calls.filterKeys(invComparator).values.flatten.size > 0

      if (shouldContinue)
        goNextFloor
      else if (shouldReverse)
        reverse.command
      else
        goToDefaultFloor  // nothing special to do, return to the default floor
    }

  override def toString = {
      "Door status: " + (if (doorIsOpened) "OPENED" else "CLOSED")  +
      "\nFloor: " + currentFloor +
      "\nDirection: " + currentDirection +
      "\nPassengers: " + destinations +
      "\nCalls: " + calls + "\n"
  }
}
