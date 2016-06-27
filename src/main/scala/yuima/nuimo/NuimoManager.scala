//// Copyright (c) 2016 Yuichiroh Matsubayashi
//
//package yuima.nuimo
//
//import yuima.nuimo.action.SystemAction
//import yuima.nuimo.config.Config.HandlerID
//import yuima.nuimo.config._
//import scala.sys.process._
//
//import scala.io.Source
//
//object NuimoManager {
//  val actionInterval = 2e8
//
//  val uuid2config =
//    upickle.default.read[Seq[NuimoConfig]](Source.fromFile("config/nuimo_config.txt").getLines().mkString("\n"))
//    .map(config => config.uuid -> config).toMap
//
//  var imgTag: String = ""
//  var appName = SystemAction.getActiveAppName
//  var lastEventTimeStamp = System.nanoTime()
//  var currentVolume = SystemAction.getVolume
//
////  def main(): Unit = {
////    println("Nuimo Manager")
////    val uuids = uuid2config.keys
////    //    yuima.nuimo.init(uuids)
////  }
//
//  def handler(peripheralUUID: String, serviceUUID: String, data: Any = null) = {
//    if (hasSufficientEventInterval)
//      appName = SystemAction.getActiveAppName
//
//    val pHandler =
//      Config.id2handler(HandlerID.withName(
//        uuid2config(peripheralUUID).handlers.getOrElse(appName, HandlerID.Default.toString)
//      ))
//
//    import NuimoUUID._
//    serviceUUID match {
//      case Characteristics.BUTTON_CLICK => pHandler.onClick(peripheralUUID, data)
//      case Characteristics.ROTATION => pHandler.onRotate(peripheralUUID, data)
//      case Characteristics.SWIPE => pHandler.onSwipe(peripheralUUID, data)
//      case Characteristics.FLY => pHandler.onFly(peripheralUUID, data)
//      case Events.CONNECTED => pHandler.onConnect(peripheralUUID)
//      case Events.DISCONNECTED => pHandler.onDisconnect(peripheralUUID)
//      case _ =>
//    }
//    lastEventTimeStamp = System.nanoTime()
//  }
//
//  def hasSufficientEventInterval: Boolean = {
//    System.nanoTime() - lastEventTimeStamp > actionInterval
//  }
//
//  def hasSufficientEventInterval(interval: Double): Boolean = {
//    System.nanoTime() - lastEventTimeStamp > interval
//  }
//
//  def showBatteryStatus(uuid: String) = {}
//
//  //  nuimo.batteryStatus(uuid)
//
//  def printBatteryStatus(uuid: String, voltage: Int) = {
//    println( s"""Battery: $voltage %""")
//  }
//
//  def writeLedImage(uuid: String, img: LedImage) = {}
//
//  //    nuimo.writeToLEDs(uuid, img.state.toJSArray, img.brightness, img.duration)
//
//  def writeLedImage(uuid: String, img: Seq[Int], brightness: Int = 75, duration: Int = 10) = {}
//
//  //    nuimo.writeToLEDs(uuid, img.toJSArray, brightness, duration)
//
//  def runAppleScriptSync(script: String) = {
//    val cmd = s"osascript -ss -e $script"
//    (cmd + script !!).trim.replace("\"", "")
//  }
//  //    se.execStringSync(script).toString
//
//  def runAppleScript(script: String): Unit = {
//    val cmd = s"osascript -ss -e $script"
//    (cmd + script).run()
//  }
//  // se.execString(script)
//}
