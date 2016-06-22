// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.NuimoManager

object SystemAction {
  def sendNotification(message: String, subtitle: String = "", title: String = "Nuimo") = {
    val cmd = s"""display notification "$message" with title "$title" subtitle "$subtitle""""
    NuimoManager.runAppleScript(cmd)
  }

  def getVolume = {
    val cmd = "return output volume of (get volume settings)"
    NuimoManager.runAppleScriptSync(cmd).toInt
  }

  def changeVolume(value: Int) = {
    val cmd = s"set volume  $value / 100.0 * 7"
    NuimoManager.runAppleScript(cmd)
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
    NuimoManager.runAppleScriptSync(cmd).toBoolean
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
    NuimoManager.runAppleScript(cmd)
  }

  def getActiveAppName = {
    val cmd = """return path to frontmost application as text"""
    NuimoManager.runAppleScriptSync(cmd).split(":").reverse.find(_ != "") match {
      case Some(app) => app
      case None => ""
    }
  }
}
