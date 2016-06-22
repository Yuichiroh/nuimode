// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.NuimoManager

trait SimpleNuimoAction {
  def script: String

  def runScript = {
    println(script)
    NuimoManager.runAppleScript(script)
  }

  def runScriptSync = {
    NuimoManager.runAppleScriptSync(script)
  }
}

case class AppleScript(script:String) extends SimpleNuimoAction