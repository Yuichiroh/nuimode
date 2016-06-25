// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.NuimoManager
import yuima.nuimo.action.{ItunesAction, SystemAction}
import yuima.nuimo.config.LedImage

class DefaultHandler extends NuimoHandler {

  override val leftRotationSensitivity: Int = 12
  override val rightRotationSensitivity: Int = 12
  override val actionSpeed: Int = 10

  override def onPress(uuid: String) = {}

  override def onRelease(uuid: String): Unit = {
    ItunesAction.fadeInOut(uuid)
  }

  override def onSwipeLeft(uuid: String): Unit = {
    ItunesAction.prevTrack(uuid)
  }

  override def onSwipeRight(uuid: String): Unit = {
    ItunesAction.nextTrack(uuid)
  }

  override def onSwipeUp(uuid: String): Unit = {
    ItunesAction.activate
  }

  override def onSwipeDown(uuid: String): Unit = {
    if (SystemAction.isMuted)
      NuimoManager.writeLedImage(uuid, LedImage.unmute)
    else
      NuimoManager.writeLedImage(uuid, LedImage.mute)
    SystemAction.mute
  }

  override def onRotateLeft(uuid: String, velocity: Int) = SystemAction.changeVolume(uuid, velocity)

  override def onRotateRight(uuid: String, velocity: Int) = SystemAction.changeVolume(uuid, velocity)

  override def onPressRotateLeft(uuid: String, velocity: Int): Unit = {}

  override def onPressRotateRight(uuid: String, velocity: Int): Unit = {}

  override def onFlyLeft(uuid: String): Unit = {}

  override def onFlyRight(uuid: String): Unit = {}

  override def onFlyTowards(uuid: String): Unit = {}

  override def onFlyBackwards(uuid: String): Unit = {}

  override def onFlyHover(uuid: String): Unit = {}
}