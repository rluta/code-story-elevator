package models

/**
 * Utility class for describing the elevator direction and simplify tests related to direction
 */
case class Direction(var direction: String, var command: Command, var floorCompare: (Int) => (Int) => Boolean) {

  override def toString = direction

  def inverse = if (this == Direction.UP)
    Direction.DOWN
  else
    Direction.UP

  def ==(other: Direction):Boolean = direction == other.direction

  def compareTo(floor: Int): Int => Boolean = floorCompare(floor)

}

object Direction extends Enumeration {
  def byName(dir: String):Direction =
      if (dir == "UP")
        Direction.UP
      else if (dir == "DOWN")
        Direction.DOWN
      else
        throw new IllegalArgumentException("Unknown direction "+dir)
  val DOWN:Direction = Direction("DOWN",Command.DOWN, { x => y => y < x})
  val UP:Direction = Direction("UP",Command.UP, { x => y => x < y})
}
