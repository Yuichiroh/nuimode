// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.action.{Key, KeyCode, KeyCodes}

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/17.
  */
object LightroomHandler extends DefaultHandler {
  override val leftRotationSensitivity: Int = 20
  override val rightRotationSensitivity: Int = 20
  override val actionSpeed = 1
  val sliderDelta = 5
  val largeDelta = 20

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

  override def onRotateLeft(uuid: String, velocity: Int): Unit = {
    moveSlider(math.abs(velocity), KeyCode(Key.Minus))
  }

  def moveSlider(velocity: Int, key: KeyCode): Unit = {
    println(velocity)
    val large = velocity / largeDelta
    val normal = velocity / sliderDelta
    val small = velocity % sliderDelta

    if (large > 0) KeyCodes(Seq.fill(large)(key.withShift)).runScript
    if (normal > 0) KeyCodes(Seq.fill(normal)(key)).runScript
    if (small > 0) KeyCodes(Seq.fill(small)(key.withOpt)).runScript
  }

  override def onPressRotateLeft(uuid: String, velocity: Int): Unit = {
    KeyCodes(Seq.fill(math.abs(velocity / 2))(KeyCode(Key.Minus).withShift)).runScript
  }

  override def onRotateRight(uuid: String, velocity: Int): Unit = {
    moveSlider(velocity, KeyCode(Key.Equal))
  }

  override def onPressRotateRight(uuid: String, velocity: Int): Unit = {
    KeyCodes(Seq.fill(velocity / 2)(KeyCode(Key.Equal).withShift)).runScript
  }

  override def onRelease(uuid: String): Unit = {
    KeyCode(Key.Period).runScript
  }
}
