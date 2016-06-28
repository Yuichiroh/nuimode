// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.Nuimode
import yuima.nuimo.action.{ItunesAction, SystemAction}
import yuima.nuimo.config.LedImage

class DefaultHandler extends NuimoHandler {

  override val leftRotationSensitivity: Int = 12
  override val rightRotationSensitivity: Int = 12
  override val actionSpeed: Int = 10

  override def onPress(client: Nuimode, uuid: String) = {}

  override def onRelease(client: Nuimode, uuid: String): Unit = {
    ItunesAction.fadeInOut(client, uuid)
  }

  override def onSwipeLeft(client: Nuimode, uuid: String): Unit = {
    ItunesAction.prevTrack(client, uuid)
  }

  override def onSwipeRight(client: Nuimode, uuid: String): Unit = {
    ItunesAction.nextTrack(client, uuid)
  }

  override def onSwipeUp(client: Nuimode, uuid: String): Unit = {
    ItunesAction.activate
  }

  override def onSwipeDown(client: Nuimode, uuid: String): Unit = {
    if (SystemAction.isMuted)
      client.writeLedImage(uuid, LedImage.unmute)
    else
      client.writeLedImage(uuid, LedImage.mute)
    SystemAction.mute
  }

  override def onRotateLeft(client: Nuimode, uuid: String, velocity: Int) = SystemAction.changeVolume(client, uuid,
                                                                                                      velocity)

  override def onRotateRight(client: Nuimode, uuid: String, velocity: Int) = SystemAction.changeVolume(client, uuid,
                                                                                                       velocity)

  override def onPressRotateLeft(client: Nuimode, uuid: String, velocity: Int): Unit = {}

  override def onPressRotateRight(client: Nuimode, uuid: String, velocity: Int): Unit = {}

  override def onFlyLeft(client: Nuimode, uuid: String): Unit = {}

  override def onFlyRight(client: Nuimode, uuid: String): Unit = {}

  override def onFlyTowards(client: Nuimode, uuid: String): Unit = {}

  override def onFlyBackwards(client: Nuimode, uuid: String): Unit = {}

  override def onFlyHover(client: Nuimode, uuid: String): Unit = {}
}