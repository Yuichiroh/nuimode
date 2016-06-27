// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.Client
import yuima.nuimo.action.{AppleScript, Key, KeyCode, KeyCodes}

object PowerPointHandler extends DefaultHandler {
  override val leftRotationSensitivity: Int = 50
  override val rightRotationSensitivity: Int = 50
  var _isSlideShowMode = false

  override def onRelease(client:Client, uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.RightArrow).runScript
      case false => KeyCode(Key.Return).withCmd.runScript
    }
  }

  override def onSwipeLeft(client:Client, uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.LeftArrow).runScript
      case false => KeyCode(Key.LeftArrow).withCtrl.withCmd.runScript
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

  override def onSwipeRight(client:Client, uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.RightArrow).runScript
      case false => KeyCode(Key.RightArrow).withCtrl.withCmd.runScript
    }
  }

  override def onSwipeUp(client:Client, uuid: String): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCode(Key.UpArrow).withCtrl.withCmd.runScript
    }
  }

  override def onSwipeDown(client:Client, uuid: String): Unit = {
    isSlideShowMode match {
      case true => KeyCode(Key.Escape).runScript
      case false => KeyCode(Key.DownArrow).withCtrl.withCmd.runScript
    }
  }

  override def onRotateLeft(client:Client, uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.UpArrow))).runScript
    }
  }

  override def onPressRotateLeft(client:Client, uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.LeftArrow))).runScript
      case false => KeyCodes(Seq.fill(math.abs(velocity))(KeyCode(Key.UpArrow))).runScript
    }
  }

  override def onRotateRight(client:Client, uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true =>
      case false => KeyCodes(Seq.fill(velocity)(KeyCode(Key.DownArrow))).runScript
    }
  }

  override def onPressRotateRight(client:Client, uuid: String, velocity: Int): Unit = {
    isSlideShowMode match {
      case true => KeyCodes(Seq.fill(velocity)(KeyCode(Key.RightArrow))).runScript
      case false => KeyCodes(Seq.fill(velocity)(KeyCode(Key.DownArrow))).runScript
    }
  }
}
