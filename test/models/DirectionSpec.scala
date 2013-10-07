package models

import org.specs2.mutable._
import scala.collection._

class DirectionSpec extends Specification {

  "Direction" should {
    "be built from String" in {
      Direction.byName("UP") must be equalTo Direction.UP
      Direction.byName("DOWN") must be equalTo Direction.DOWN
    }
    "have meaningful toString" in {
      Direction.UP.toString must be equalTo "UP"
    }
    "allow inverse" in {
      Direction.UP.inverse must be equalTo Direction.DOWN
      Direction.DOWN.inverse must be equalTo Direction.UP
    }
    "filter floors correctly" in {
      List(0,1,2,3).filter(Direction.UP.compareTo(0)) must be equalTo List(1,2,3)
      List(0,1,2,3).filter(Direction.DOWN.compareTo(2)) must be equalTo List(0,1)
    }
  }
}
