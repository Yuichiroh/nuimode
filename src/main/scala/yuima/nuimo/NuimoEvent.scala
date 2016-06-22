// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo

object NuimoEvent {

  object Swipe extends Enumeration {
    val LEFT, RIGHT, UP, DOWN = Value
  }

  object Fly extends Enumeration {
    val LEFT, RIGHT, BACKWARDS, TOWARDS, HOVER, DOWN = Value
  }

  object Rotate extends Enumeration {
    val LEFT, RIGHT = Value
  }

  object Click extends Enumeration {
    val RELEASE, PRESS = Value
  }

}
