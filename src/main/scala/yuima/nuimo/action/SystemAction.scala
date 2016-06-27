// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.{Client, Nuimode}
import yuima.nuimo.config.LedImage._

object SystemAction {
  def sendNotification(message: String, subtitle: String = "", title: String = "Nuimo") = {
    val cmd = s"""display notification "$message" with title "$title" subtitle "$subtitle""""
    Nuimode.runAppleScript(cmd)
  }

  def changeVolume(client:Client, uuid: String, delta: Int) = {
    def changeVolume(value: Int) = {
      val cmd = s"set volume  $value / 100.0 * 7"
      Nuimode.runAppleScript(cmd)
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

    if (Nuimode.hasSufficientEventInterval(Nuimode.actionInterval * 10))
      Nuimode.currentVolume = SystemAction.getVolume

    val volume = ((Nuimode.currentVolume + delta) max 0) min 100
    val nv = normalizedVolume(volume)
    val nvcv = normalizedVolume(Nuimode.currentVolume)

    if (Nuimode.imgTag != "volume" || nvcv != nv) {
      client.writeLedImage(uuid, volumeImage(nv))
      Nuimode.imgTag = "volume"
    }

    if (volume != Nuimode.currentVolume) {
      changeVolume(volume)
      Nuimode.currentVolume = volume
    }
  }

  def getVolume = {
    val cmd = "return output volume of (get volume settings)"
    Nuimode.runAppleScriptSync(cmd).toInt
  }

  def isMuted = {
    val cmd =
      """set volumeSettings to get volume settings
        |if output muted of volumeSettings is false then
        |  return false
        |else
        |  return true
        |end if
      """.stripMargin
    Nuimode.runAppleScriptSync(cmd).toBoolean
  }

  def mute = {
    val cmd =
      """set volumeSettings to get volume settings
        |if output muted of volumeSettings is false then
        |  set volume with output muted
        |else
        |  set volume without output muted
        |end if
      """.stripMargin
    Nuimode.runAppleScript(cmd)
  }

  def getActiveAppName = {
    val cmd = """return path to frontmost application as text"""
    Nuimode.runAppleScriptSync(cmd).split(":").reverse.find(_ != "") match {
      case Some(app) => app
      case None => ""
    }
  }
}
