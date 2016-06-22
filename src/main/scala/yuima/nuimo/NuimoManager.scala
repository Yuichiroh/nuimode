// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo

import yuima.nuimo.action.SystemAction
import yuima.nuimo.config.Config.HandlerID
import yuima.nuimo.config._

import scala.scalajs.js.JSApp
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport

object NuimoManager extends JSApp {
  val se = new AppleScriptEngine
  val fileIO = new IO
  val nuimo = new Nuimo
  val actionInterval = 2e8

  val uuid2config =
    upickle.default.read[Seq[NuimoConfig]](fileIO.readFromFile("config/nuimo_config.txt"))
      .map(config => config.uuid -> config).toMap

  var imgTag: String = ""
  var appName = SystemAction.getActiveAppName
  var lastEventTimeStamp = System.nanoTime()
  var currentVolume = SystemAction.getVolume

  def main(): Unit = {
    println("Nuimo Manager")
    val uuids = uuid2config.keys.toJSArray
    nuimo.init(uuids)
  }

  @JSExport
  def handler(peripheralUUID: String, serviceUUID: String, data: Any = null) = {
    if (hasSufficientEventInterval)
      appName = SystemAction.getActiveAppName

    val pHandler =
      Config.id2handler(
        HandlerID.withName(
          uuid2config(peripheralUUID).handlers.getOrElse(appName, HandlerID.Default.toString)
        )
      )
    import NuimoUUID._
    serviceUUID match {
      case Characteristics.BUTTON_CLICK => pHandler.onClick(peripheralUUID, data)
      case Characteristics.ROTATION => pHandler.onRotate(peripheralUUID, data)
      case Characteristics.SWIPE => pHandler.onSwipe(peripheralUUID, data)
      case Characteristics.FLY => pHandler.onFly(peripheralUUID, data)
      case Events.CONNECTED => pHandler.onConnect(peripheralUUID)
      case Events.DISCONNECTED => pHandler.onDisconnect(peripheralUUID)
      case _ =>
    }
    lastEventTimeStamp = System.nanoTime()
  }

  def hasSufficientEventInterval: Boolean = {
    System.nanoTime() - lastEventTimeStamp > actionInterval
  }

  def hasSufficientEventInterval(interval: Double): Boolean = {
    System.nanoTime() - lastEventTimeStamp > interval
  }

  def batteryStatus(uuid: String) = nuimo.batteryStatus(uuid)

  def writeLedImage(uuid: String, img: LedImage) =
    nuimo.writeToLEDs(uuid, img.state.toJSArray, img.brightness, img.duration)

  def writeLedImage(uuid: String, img: Seq[Int], brightness: Int = 75, duration: Int = 10) =
    nuimo.writeToLEDs(uuid, img.toJSArray, brightness, duration)

  def runAppleScriptSync(script: String) =
    se.execStringSync(script).toString.trim.replace("\"", "")

  def runAppleScript(script: String) = se.execString(script)
}
