// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.action

sealed class Key(val code: Int, val char: String = null) {
  val symbol = if (char != null) char else toString
}

object Key {

  object Modifier extends Enumeration {
    val command, control, shift, option = Value
  }

  case object Zero extends Key(29, "0")

  case object One extends Key(18, "1")

  case object Two extends Key(19, "2")

  case object Three extends Key(20, "3")

  case object Four extends Key(21, "4")

  case object Five extends Key(23, "5")

  case object Six extends Key(22, "6")

  case object Seven extends Key(26, "7")

  case object Eight extends Key(28, "8")

  case object Nine extends Key(25, "9")

  case object A extends Key(0)

  case object B extends Key(11)

  case object C extends Key(8)

  case object D extends Key(2)

  case object E extends Key(14)

  case object F extends Key(3)

  case object G extends Key(5)

  case object H extends Key(4)

  case object I extends Key(34)

  case object J extends Key(38)

  case object K extends Key(40)

  case object L extends Key(37)

  case object M extends Key(46)

  case object N extends Key(45)

  case object O extends Key(31)

  case object P extends Key(35)

  case object Q extends Key(12)

  case object R extends Key(15)

  case object S extends Key(1)

  case object T extends Key(17)

  case object U extends Key(32)

  case object V extends Key(9)

  case object W extends Key(13)

  case object X extends Key(7)

  case object Y extends Key(16)

  case object Z extends Key(6)

  case object SectionSign extends Key(10)

  case object Grave extends Key(50)

  case object Minus extends Key(27)

  case object Equal extends Key(24)

  case object LeftBracket extends Key(33)

  case object RightBracket extends Key(30)

  case object Semicolon extends Key(41)

  case object Quote extends Key(39)

  case object Comma extends Key(43, ",")

  case object Period extends Key(47, ".")

  case object Slash extends Key(44, "/")

  case object Backslash extends Key(42, "\\")

  case object Keypad0 extends Key(82, "0")

  case object Keypad1 extends Key(83, "1")

  case object Keypad2 extends Key(84, "2")

  case object Keypad3 extends Key(85, "3")

  case object Keypad4 extends Key(86, "4")

  case object Keypad5 extends Key(87, "5")

  case object Keypad6 extends Key(88, "6")

  case object Keypad7 extends Key(89, "7")

  case object Keypad8 extends Key(91, "8")

  case object Keypad9 extends Key(92, "9")

  case object KeypadDecimal extends Key(65)

  case object KeypadMultiply extends Key(67)

  case object KeypadPlus extends Key(69)

  case object KeypadDivide extends Key(75)

  case object KeypadMinus extends Key(78)

  case object KeypadEquals extends Key(81)

  case object KeypadClear extends Key(71)

  case object KeypadEnter extends Key(76)

  case object Space extends Key(49)

  case object Return extends Key(36)

  case object Tab extends Key(48)

  case object Delete extends Key(51)

  case object ForwardDelete extends Key(117)

  case object Linefeed extends Key(52)

  case object Escape extends Key(53)

  case object Command extends Key(55)

  case object Shift extends Key(56)

  case object CapsLock extends Key(57)

  case object Option extends Key(58)

  case object Control extends Key(59)

  case object RightShift extends Key(60)

  case object RightOption extends Key(61)

  case object RightControl extends Key(62)

  case object Function extends Key(63, "fn")

  case object F1 extends Key(122)

  case object F2 extends Key(120)

  case object F3 extends Key(99)

  case object F4 extends Key(118)

  case object F5 extends Key(96)

  case object F6 extends Key(97)

  case object F7 extends Key(98)

  case object F8 extends Key(100)

  case object F9 extends Key(101)

  case object F10 extends Key(109)

  case object F11 extends Key(103)

  case object F12 extends Key(111)

  case object F13 extends Key(105)

  case object BrightnessDown extends Key(107)

  case object BrightnessUp extends Key(113)

  case object F16 extends Key(106)

  case object F17 extends Key(64)

  case object F18 extends Key(79)

  case object F19 extends Key(80)

  case object F20 extends Key(90)

  case object VolumeUp extends Key(72)

  case object VolumeDown extends Key(73)

  case object Mute extends Key(74)

  case object Insert extends Key(114)

  case object Home extends Key(115)

  case object End extends Key(119)

  case object PageUp extends Key(116)

  case object PageDown extends Key(121)

  case object LeftArrow extends Key(123)

  case object RightArrow extends Key(124)

  case object DownArrow extends Key(125)

  case object UpArrow extends Key(126)

}
