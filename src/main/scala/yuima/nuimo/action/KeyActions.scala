// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

trait KeyAction extends SimpleNuimoAction {
  val action: String
  val modifierKeys: Set[Key.Modifier.Value]

  def script = s"""tell application "System Events" to $action$modifierString"""

  def modifierString =
    if (modifierKeys.isEmpty) ""
    else
      modifierKeys.map(_ + " down").mkString(" using {", ", ", "}")

  def withCtrl: KeyAction

  def withCmd: KeyAction

  def withOpt: KeyAction

  def withShift: KeyAction
}

case class KeyStroke(stroke: String,
                     modifierKeys: Set[Key.Modifier.Value] = Set.empty[Key.Modifier.Value])
  extends KeyAction {

  val action = s"""keystroke "$stroke""""

  def withCtrl = KeyStroke(stroke, modifierKeys + Key.Modifier.control)

  def withCmd = KeyStroke(stroke, modifierKeys + Key.Modifier.command)

  def withOpt = KeyStroke(stroke, modifierKeys + Key.Modifier.option)

  def withShift = KeyStroke(stroke, modifierKeys + Key.Modifier.shift)
}

case class KeyCode(key: Key,
                   modifierKeys: Set[Key.Modifier.Value] = Set.empty[Key.Modifier.Value])
  extends KeyAction {

  val action = s"""key code ${ key.code }"""

  def withCtrl = KeyCode(key, modifierKeys + Key.Modifier.control)

  def withCmd = KeyCode(key, modifierKeys + Key.Modifier.command)

  def withOpt = KeyCode(key, modifierKeys + Key.Modifier.option)

  def withShift = KeyCode(key, modifierKeys + Key.Modifier.shift)

  def toKeyStroke = KeyStroke(key.symbol, modifierKeys)
}

case class KeyCodes(keys: Seq[KeyCode]) extends SimpleNuimoAction {
  override def runScript: Any = if (keys.nonEmpty) super.runScript

  def script =
    "tell application \"System Events\"\n" +
    s"${ keys.map(k => k.action + k.modifierString).mkString("\n") }\n" +
    "end tell"

  override def runScriptSync: String = if (keys.nonEmpty) super.runScriptSync else ""
}