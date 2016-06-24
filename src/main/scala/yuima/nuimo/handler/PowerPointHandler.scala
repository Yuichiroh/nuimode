// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.action.{AppleScript, Key, KeyCode, KeyCodes}
import yuima.nuimo.{NuimoEvent, NuimoManager}

import scala.scalajs.js.Array

object PowerPointHandler extends DefaultHandler {
  override val leftRotationSensitivity: Int = 50
  override val rightRotationSensitivity: Int = 50
  val actionSpeed = 10
  var _isSlideShowMode = false
  var lastValidActionTimeStamp = System.nanoTime()
  var totalVelocity = 0
  var click = 0

  override def onRelease(uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.RightArrow).runScript
      case false =>
        KeyCode(Key.Return).withCmd.runScript
        lastValidActionTimeStamp = System.nanoTime()
    }
  }

  override def onSwipeLeft(uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.LeftArrow).runScript
      case false => KeyCode(Key.LeftArrow).withCtrl.withCmd.runScript
    }
  }

  override def onSwipeRight(uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.RightArrow).runScript
      case false => KeyCode(Key.RightArrow).withCtrl.withCmd.runScript
    }
  }

  override def onSwipeUp(uuid: String): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCode(Key.UpArrow).withCtrl.withCmd.runScript
    }
  }

  override def onSwipeDown(uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.Escape).runScript
      case false => KeyCode(Key.DownArrow).withCtrl.withCmd.runScript
    }
  }

  def isSlideShowMode = {
    if (hasSufficientActionInterval)
      _isSlideShowMode =
        AppleScript( """tell application "Microsoft PowerPoint"
                       |	set slideshow to slide show window of active presentation
                       |	if slideshow is missing value then
                       |		return false
                       |	else
                       |		return true
                       |	end if
                       |end tell
                     """.stripMargin).runScriptSync.toBoolean
    _isSlideShowMode
  }

  def hasSufficientActionInterval =
    System.nanoTime() - lastValidActionTimeStamp > NuimoManager.actionInterval / actionSpeed

  override def onRotate(uuid: String, data: Any): Unit = {
    val signals = data.asInstanceOf[Array[Int]].toArray
    val direction =
      if (signals(1) == 255) NuimoEvent.Rotate.LEFT
      else NuimoEvent.Rotate.RIGHT
    val velocity = signals(0) - signals(1)

    direction match {
      case NuimoEvent.Rotate.LEFT =>
        if (isPressed) onRotate(uuid, onPressRotateLeft _, velocity, leftRotationSensitivity)
        else onRotate(uuid, onRotateLeft _, velocity, leftRotationSensitivity)
      case NuimoEvent.Rotate.RIGHT =>
        if (isPressed) onRotate(uuid, onPressRotateRight _, velocity, rightRotationSensitivity)
        else onRotate(uuid, onRotateRight _, velocity, rightRotationSensitivity)
    }
  }

  override def onRotateLeft(uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.UpArrow))).runScript
    }
  }

  override def onPressRotateLeft(uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.LeftArrow))).runScript
      case false => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.UpArrow))).runScript
    }
  }

  override def onRotateRight(uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCodes(Seq.fill(velocity)(KeyCode(Key.DownArrow))).runScript
    }
  }

  override def onPressRotateRight(uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true => KeyCodes(Seq.fill(velocity)(KeyCode(Key.RightArrow))).runScript
      case false => KeyCodes(Seq.fill(velocity)(KeyCode(Key.DownArrow))).runScript
    }
  }

  def onRotate(velocity: Int, keyOnSlideShow: KeyCode, keyOnElse: KeyCode, sensitivity: Int): Unit = {
    if (NuimoManager.hasSufficientEventInterval) totalVelocity = 0
    if (hasSufficientActionInterval && math.abs(totalVelocity) > sensitivity) {
      isSlideShowMode match {
        case true =>
          if (isPressed)
            KeyCodes(Seq.fill(math.abs(totalVelocity) / sensitivity)(keyOnSlideShow)).runScript
          else {}
        case false => KeyCodes(Seq.fill(math.abs(totalVelocity) / sensitivity)(keyOnElse)).runScript
      }
      lastValidActionTimeStamp = System.nanoTime()
      totalVelocity %= sensitivity
    }
    else {
      totalVelocity += velocity
    }
  }

  def onRotate(uuid: String, action: (String, Int) => Unit, velocity: Int, sensitivity: Int): Unit = {
    if (NuimoManager.hasSufficientEventInterval) totalVelocity = 0
    totalVelocity += velocity

    if (hasSufficientActionInterval && math.abs(totalVelocity) > sensitivity) {
      action(uuid, totalVelocity / sensitivity)
      lastValidActionTimeStamp = System.nanoTime()
      totalVelocity %= sensitivity
    }
  }
}
