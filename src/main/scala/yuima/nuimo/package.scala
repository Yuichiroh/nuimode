// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima

import yuima.nuimo.handler.DefaultHandler

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

package object nuimo {
  val defaultHandler = new DefaultHandler

  @JSName("nuimo")
  @js.native
  class Nuimo extends js.Object {
    def init(uuids: js.Array[String]): Unit = js.native

    def createDataForLedMatrix(data: Any, brightness: Int, duration: Int): Any = js.native

    def writeToLEDs(uuid: String, img: js.Array[Int], brightness: Int = 75, duration: Int = 10): Unit = js.native

    def batteryStatus(uuid: String): Int = js.native
  }

  @JSName("applescript")
  @js.native
  class AppleScriptEngine extends js.Object {
    def execString(cmd: String): Any = js.native

    def execStringSync(cmd: String): Any = js.native
  }

  @JSName("io")
  @js.native
  class IO extends js.Object {
    def readFromFile(file: String): String = js.native

    def writeToFile(file: String, data: String): Unit = js.native
  }

}
