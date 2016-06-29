// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo

import java.net.InetSocketAddress
import java.util.concurrent.Executors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import yuima.nuimo.action.SystemAction
import yuima.nuimo.config.Config.HandlerID
import yuima.nuimo.config.{Config, LedImage, NuimoConfig, NuimoUUID}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.sys.process._

object Nuimode {
  type OptionMap = Map[Symbol, Any]
  val system = ActorSystem.create()

  val uuid2config =
    upickle.default.read[Seq[NuimoConfig]](Source.fromFile("config/nuimo_config.json").getLines().mkString("\n"))
    .map(config => config.uuid -> config).toMap
  val uuids = uuid2config.keys

  var imgTag: String = ""
  var appName = SystemAction.getActiveAppName
  var lastEventTimeStamp = System.nanoTime()
  var currentVolume = SystemAction.getVolume

  def nextOption(map: OptionMap, list: List[String]): OptionMap = list match {
    case Nil => map
    case "-p" :: _port :: tail =>
      nextOption(map ++ Map('port -> _port.toInt), tail)
    case "-a" :: addr :: tail =>
      nextOption(map ++ Map('address -> addr), tail)
    case option :: tail =>
      println("Unknown option " + option)
      System.exit(1)
      map
  }

  def main(args: Array[String]): Unit = {
    val options = nextOption(defalultOptions, args.toList)

    val port = options.get('port) match {
      case Some(p) => p.asInstanceOf[Int]
      case None => 3000
    }
    val address = options.get('address) match {
      case Some(a) => a.asInstanceOf[String]
      case None => "localhost"
    }

    Console.err.println("Nuimo Manager")
    Console.err.println(s"listening on $address:$port")
    "node src/main/resources/js/server.js".run
    val client = system.actorOf(Props(classOf[Nuimode], new InetSocketAddress(address, port)))
  }

  def defalultOptions: OptionMap = Map()

  import scala.sys.process._

  def runAppleScriptSync(script: String): String = {
    (Seq("echo", script) #> "osascript -ss" !!).trim.replace("\"", "")
  }

  def runAppleScript(script: String): Unit = {
    (Seq("echo", script) #> "osascript -ss").run()
  }

  def hasSufficientEventInterval: Boolean = {
    System.nanoTime() - lastEventTimeStamp > Config.actionInterval.milli.toNanos
  }

  def hasSufficientEventInterval(nanoTime: Long): Boolean = {
    System.nanoTime() - lastEventTimeStamp > nanoTime
  }

  def props(remote: InetSocketAddress) =
    Props(classOf[Nuimode], remote)
}

class Nuimode(remote: InetSocketAddress) extends Actor {
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(Config.numThreadPool))

  import Nuimode._
  import context.system

  var connection: ActorRef = _

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      //      sys.error("connect failed")
//      context stop self
//      system.terminate()
      IO(Tcp) ! Connect(remote)
    case c@Connected(_remote, local) =>
      connection = sender
      connection ! Register(self)

      context become {
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          sys.error("write failed")
          system.terminate()
        case Received(data) =>
          var str = ByteString()
          str ++= data
          val signals = str.decodeString("utf-8").split(";")
          for (s <- signals) {
            val msg = upickle.default.read[Message](s)
            handler(msg)
          }
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          sys.error("connection closed")
          context stop self
          system.terminate()
      }
  }

  def handler(message: Message) = {
    val Message(peripheralUUID, serviceUUID, data) = message

    if (hasSufficientEventInterval)
      appName = SystemAction.getActiveAppName

    val pHandler =
      Config.id2handler(HandlerID.withName(
        uuid2config(peripheralUUID).handlers.getOrElse(appName, HandlerID.Default.toString)
      ))

    import NuimoUUID._

    Future {
      serviceUUID match {
        case Characteristics.BUTTON_CLICK => pHandler.onClick(this, peripheralUUID, data)
        case Characteristics.ROTATION => pHandler.onRotate(this, peripheralUUID, data)
        case Characteristics.SWIPE => pHandler.onSwipe(this, peripheralUUID, data)
        case Characteristics.FLY => pHandler.onFly(this, peripheralUUID, data)
        case Characteristics.BATTERY => printBatteryStatus(peripheralUUID, data(0))
        case Events.CONNECTED => pHandler.onConnect(peripheralUUID)
        case Events.DISCONNECTED => pHandler.onDisconnect(peripheralUUID)
        case _ =>
      }
      lastEventTimeStamp = System.nanoTime()
    }
  }

  def printBatteryStatus(uuid: String, voltage: Int) = println( s"""Battery: $voltage %""")

  def writeLedImage(uuid: String, img: LedImage): Unit = {
    val LedImage(image, brightness, duration) = img
    writeLedImage(uuid, image, brightness, duration)
  }

  def writeLedImage(uuid: String, img: Seq[Int], brightness: Int = 75, duration: Int = 10): Unit =
    send(s"$uuid:led:${ img.mkString("") }:$brightness:$duration")

  def showBatteryStatus(uuid: String) = send(s"$uuid:battery")

  def send(data: String) = connection ! Write(ByteString(data))
}
