// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.NuimoManager
import yuima.nuimo.action.ChromeYoutubeAction
import yuima.nuimo.config.LedImage

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/20.
  */
object GoogleChromeHandler extends DefaultHandler {
  override def onRelease(uuid: String) = {
    println("chrome release")
    val success = ChromeYoutubeAction.tryPlaypause
    if (success)
      NuimoManager.writeLedImage(uuid, LedImage.youtube)
    else
      super.onRelease(uuid)
  }

  override def onSwipeRight(uuid: String): Unit = {
    println("chrome sr")
    val success = ChromeYoutubeAction.tryNextTrack
    if (success)
      NuimoManager.writeLedImage(uuid, LedImage.forward)
    else
      super.onSwipeRight(uuid)
  }

  override def onSwipeLeft(uuid: String): Unit = {
    println("chrome sl")
    val success = ChromeYoutubeAction.tryPrevTrack
    if (success)
      NuimoManager.writeLedImage(uuid, LedImage.backward)
    else
      super.onSwipeLeft(uuid)
  }
}
