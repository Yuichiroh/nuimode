// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.NuimoManager
import yuima.nuimo.action.{AppleScript, Key, KeyCode, KeyCodes}

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/17.
  */
object PowerPointHandler extends NuimoHandler {
  override val leftRotationSensitivity: Int = 40
  override val rightRotationSensitivity: Int = 40
  val actionSpeed = 4
  var isPressed = false
  var _isSlideShowMode = false
  var lastValidActionTimeStamp = System.nanoTime()
  var totalVelocity = 0
  var click = 0

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

  def isSlideShowMode = {
    if (hasSufficientActionInterval)
      _isSlideShowMode = AppleScript(
        """tell application "Microsoft PowerPoint"
          |	set slideshow to slide show window of active presentation
          |	if slideshow is missing value then
          |		return false
          |	else
          |		return true
          |	end if
          |end tell
        """.stripMargin
      ).runScriptSync.toBoolean
    _isSlideShowMode
  }

  def hasSufficientActionInterval =
    System.nanoTime() - lastValidActionTimeStamp > NuimoManager.actionInterval / actionSpeed

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

  override def onFlyRight(uuid: String): Unit = {}

  override def onFlyHover(uuid: String): Unit = {}

  override def onRotateRight(uuid: String, velocity: Int): Unit = {
    onRotate(velocity, KeyCode(Key.RightArrow), KeyCode(Key.DownArrow), rightRotationSensitivity)
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

  override def onRotateLeft(uuid: String, velocity: Int): Unit = {
    onRotate(velocity, KeyCode(Key.LeftArrow), KeyCode(Key.UpArrow), leftRotationSensitivity)
  }

  override def onPress(uuid: String): Unit = {
    isPressed = true
  }

  override def onRelease(uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.RightArrow).runScript
      case false =>
        KeyCode(Key.Return).withCmd.runScript
        lastValidActionTimeStamp = System.nanoTime()
    }
    isPressed = false
  }

  override def onFlyBackwards(uuid: String): Unit = {}

  override def onFlyTowards(uuid: String): Unit = {}

  override def onFlyLeft(uuid: String): Unit = {}
}
