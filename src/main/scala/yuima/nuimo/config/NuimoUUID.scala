// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.config

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/16.
  */
object NuimoUUID {

  object Characteristics {
    val BATTERY = "00002a1900001000800000805f9b34fb"
    val DEVICE_INFO = "00002a2900001000800000805f9b34fb"
    val LED_MATRIX = "f29b1524cb1940f3be5c7241ecb82fd1"
    val ROTATION = "f29b1528cb1940f3be5c7241ecb82fd2"
    val BUTTON_CLICK = "f29b1529cb1940f3be5c7241ecb82fd2"
    val SWIPE = "f29b1527cb1940f3be5c7241ecb82fd2"
    val FLY = "f29b1526cb1940f3be5c7241ecb82fd2"
  }

  object Events {
    val CONNECTED = "Connected"
    val DISCONNECTED = "Disconnected"
  }

  object Services {
    val LED_MATRIX = "f29b1523cb1940f3be5c7241ecb82fd1"
    val USER_INPUT = "f29b1525cb1940f3be5c7241ecb82fd2"
  }

}
