// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.Nuimode
import yuima.nuimo.action.ChromeYoutubeAction
import yuima.nuimo.config.LedImage

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/20.
  */
object GoogleChromeHandler extends DefaultHandler {
  override def onRelease(client: Nuimode, uuid: String, clickCount: Int) = {
    println("chrome release")
    val success = ChromeYoutubeAction.tryPlaypause
    if (success)
      client.writeLedImage(uuid, LedImage.youtube)
    else
      super.onRelease(client, uuid, clickCount)
  }

  override def onSwipeRight(client: Nuimode, uuid: String): Unit = {
    println("chrome sr")
    val success = ChromeYoutubeAction.tryNextTrack
    if (success)
      client.writeLedImage(uuid, LedImage.forward)
    else
      super.onSwipeRight(client, uuid)
  }

  override def onSwipeLeft(client: Nuimode, uuid: String): Unit = {
    println("chrome sl")
    val success = ChromeYoutubeAction.tryPrevTrack
    if (success)
      client.writeLedImage(uuid, LedImage.backward)
    else
      super.onSwipeLeft(client, uuid)
  }
}
