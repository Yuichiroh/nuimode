// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.NuimoManager
import yuima.nuimo.action.{ItunesAction, SystemAction}
import yuima.nuimo.config.LedImage

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/15.
  */

class DefaultHandler extends NuimoHandler {

  import LedImage.{volumeCol, volumeRow}

  override val leftRotationSensitivity: Int = 12
  override val rightRotationSensitivity: Int = 12

  override def onPress(uuid: String) = {}

  override def onRotateLeft(uuid: String, velocity: Int) = changeVolume(uuid, velocity, leftRotationSensitivity)

  def changeVolume(uuid: String, velocity: Int, sensitivity: Int) = {
    if (NuimoManager.hasSufficientEventInterval(NuimoManager.actionInterval * 10))
      NuimoManager.currentVolume = SystemAction.getVolume

    val delta = velocity / sensitivity
    val volume = ((NuimoManager.currentVolume + delta) max 0) min 100
    val nv = normalizedVolume(volume)
    val nvcv = normalizedVolume(NuimoManager.currentVolume)

    if (NuimoManager.imgTag != "volume" || nvcv != nv) {
      NuimoManager.writeLedImage(uuid, volumeImage(nv))
      NuimoManager.imgTag = "volume"
    }

    if (volume != NuimoManager.currentVolume) {
      SystemAction.changeVolume(volume)
      NuimoManager.currentVolume = volume
    }
  }

  def normalizedVolume(volume: Int) = {
    val rounded = math.round(volume / 100.0 * volumeCol * volumeRow).toInt
    if (rounded == volumeCol * volumeRow)
      if (volume == 100) volumeCol * volumeRow
      else rounded - 1
    else rounded
  }

  def volumeImage(volume: Int) = {
    val marginLeft = (volumeCol - volumeRow) / 2
    val marginRight = volumeCol - volumeRow - marginLeft

    def col(left: Int, body: Int, right: Int) =
      Array.fill(left)(0) ++ Array.fill(body)(1) ++ Array.fill(volumeRow - body)(0) ++ Array.fill(right)(0)

    val on = col(marginLeft, volumeRow, marginRight)
    val off = Array.fill(volumeCol)(0)
    val top =
      if (volume % volumeRow == 0) Array.empty[Int]
      else col(marginLeft, volume % volumeRow, marginRight)

    val black = (volumeRow * volumeCol - volume) / volumeRow
    val white = volume / volumeRow
    val arr = ((1 to black).map(i => off).toArray :+ top) ++ (1 to white).map(i => on)

    arr.flatten
  }

  override def onRotateRight(uuid: String, velocity: Int) = changeVolume(uuid, velocity, rightRotationSensitivity)

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

  override def onFlyRight(uuid: String): Unit = {}

  override def onFlyHover(uuid: String): Unit = {}

  override def onRelease(uuid: String): Unit = {
    ItunesAction.fadeInOut(uuid)
  }

  override def onFlyBackwards(uuid:String): Unit = {}

  override def onFlyTowards(uuid:String): Unit = {}

  override def onFlyLeft(uuid:String): Unit = {}
}