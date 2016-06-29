// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.Nuimode
import yuima.nuimo.Nuimode._
import yuima.nuimo.config.Config
import yuima.nuimo.config.LedImage._
import scala.concurrent.duration._

object SystemAction {
  def sendNotification(message: String, subtitle: String = "", title: String = "Nuimo") = {
    val cmd = s"""display notification "$message" with title "$title" subtitle "$subtitle""""
    Nuimode.runAppleScript(cmd)
  }

  def changeVolume(client: Nuimode, uuid: String, delta: Int): Unit = {
    def changeVolume(value: Int): Unit = {
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

    if (Nuimode.hasSufficientEventInterval(Config.actionInterval.milli.toNanos * 10))
      Nuimode.currentVolume = SystemAction.getVolume

    val volume = ((Nuimode.currentVolume + delta) max 0) min 100
    val nv = normalizedVolume(volume)
    val nvcv = normalizedVolume(Nuimode.currentVolume)

    if (Nuimode.imgTag != "volume" || nvcv != nv)
      client.writeLedImage(uuid, volumeImage(nv), "volume")

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
