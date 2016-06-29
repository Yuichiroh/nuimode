// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

import yuima.nuimo.Nuimode

trait SimpleNuimoAction {
  def script: String

  def runScript(): Unit = Nuimode.runAppleScript(script)

  def runScriptSync: String = Nuimode.runAppleScriptSync(script)
}

case class AppleScript(script: String) extends SimpleNuimoAction