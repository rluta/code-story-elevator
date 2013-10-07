package models

/**
 * Enumeration of commands known to the elevator
 */
case class Command(var command: String) {
  override def toString = command
}

case object Command extends Enumeration {
  val NOTHING = Command("NOTHING")
  val OPEN = Command("OPEN")
  val CLOSE = Command("CLOSE")
  val UP = Command("UP")
  val DOWN = Command("DOWN")
}

