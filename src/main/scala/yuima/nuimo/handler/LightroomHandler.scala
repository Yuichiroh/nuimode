// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.action.{Key, KeyCode}

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/17.
  */
object LightroomHandler extends NuimoHandler {
  override val leftRotationSensitivity: Int = 50
  override val rightRotationSensitivity: Int = 50
  var isPressed = false

  override def onSwipeLeft(uuid: String): Unit = {
    KeyCode(Key.LeftArrow).runScript
  }

  override def onSwipeRight(uuid: String): Unit = {
    KeyCode(Key.RightArrow).runScript
  }

  override def onSwipeUp(uuid: String): Unit = {
    KeyCode(Key.Comma).runScript
  }

  override def onSwipeDown(uuid: String): Unit = {
    KeyCode(Key.Period).runScript
  }

  override def onFlyRight(uuid: String): Unit = {}

  override def onFlyHover(uuid: String): Unit = {}

  override def onRotateRight(uuid: String, velocity: Int): Unit = {
    println("right", velocity)
    if (velocity < rightRotationSensitivity)
      KeyCode(Key.Equal).withOpt.runScript
    else if (velocity < rightRotationSensitivity * 2) {
      KeyCode(Key.Equal).runScript
    }
    else {
      KeyCode(Key.Equal).withShift.runScript
    }
  }

  override def onRotateLeft(uuid: String, velocity: Int): Unit = {
    println("left", velocity)
    if (velocity > -leftRotationSensitivity)
      KeyCode(Key.Minus).withOpt.runScript
    else if (velocity > -2 * leftRotationSensitivity)
      KeyCode(Key.Minus).runScript
    else
      KeyCode(Key.Minus).withShift.runScript
  }

  override def onPress(uuid: String): Unit = {
    println("pressed", KeyCode(Key.Period).script)

    KeyCode(Key.Period).runScript
    isPressed = true
  }

  override def onRelease(uuid: String): Unit = {
    isPressed = false
  }

  override def onFlyBackwards(uuid: String): Unit = {}

  override def onFlyTowards(uuid: String): Unit = {}

  override def onFlyLeft(uuid: String): Unit = {}
}
