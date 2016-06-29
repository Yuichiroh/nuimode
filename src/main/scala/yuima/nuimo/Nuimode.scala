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
import scala.language.postfixOps
import scala.sys.process._

object Nuimode {
  type OptionMap = Map[Symbol, Any]

  val system = ActorSystem.create()
  val uuid2config = readConfigFile
  val uuids = uuid2config.keys
  var lastEventTimeStamp = System.nanoTime()
  var currentVolume = SystemAction.getVolume
  private var _imgTag: String = ""
  private var _appName = SystemAction.getActiveAppName

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

  def runAppleScriptSync(script: String): String =
    (Seq("echo", script) #> "osascript -ss" !!).trim.replace("\"", "")

  def runAppleScript(script: String): Unit = (Seq("echo", script) #> "osascript -ss").run()

  def hasSufficientEventInterval: Boolean = {
    System.nanoTime() - lastEventTimeStamp > Config.actionInterval.milli.toNanos
  }

  def hasSufficientEventInterval(nanoTime: Long): Boolean = {
    System.nanoTime() - lastEventTimeStamp > nanoTime
  }

  def appName = _appName

  def imgTag = _imgTag

  private def props(remote: InetSocketAddress) = Props(classOf[Nuimode], remote)

  private def readConfigFile: Map[String, NuimoConfig] = {
    upickle.default.read[Seq[NuimoConfig]](
      Source.fromFile("config/nuimo_config.json").getLines().mkString("\n")
    ).map(config => config.uuid -> config).toMap
  }

  private def nextOption(map: OptionMap, list: List[String]): OptionMap = list match {
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

  private def defalultOptions: OptionMap = Map()
}

class Nuimode(remote: InetSocketAddress) extends Actor {
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(Config.numThreadPool))

  import Nuimode._
  import context.system

  var connection: ActorRef = _

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) => IO(Tcp) ! Connect(remote)
    case c@Connected(_remote, local) =>
      connection = sender
      connection ! Register(self)
      changeContextAfterConnected()
  }

  private def changeContextAfterConnected(): Unit = {
    context become {
      case CommandFailed(w: Write) =>
        sys.error("write failed")
        system.terminate()
      case Received(data) =>
        var str = ByteString()
        str ++= data
        val signals = str.decodeString("utf-8").split(";")
        for (s <- signals) {
          val msg = upickle.default.read[Message](s)
          handle(msg)
        }
      case "close" => connection ! Close
      case _: ConnectionClosed =>
        sys.error("connection closed")
        context stop self
        system.terminate()
    }
  }

  private def handle(message: Message) = {
    val Message(peripheralUUID, serviceUUID, data) = message

    if (hasSufficientEventInterval)
      _appName = SystemAction.getActiveAppName

    val handler =
      Config.id2handler(HandlerID.withName(handlerIdStringForCurrentApp(peripheralUUID)))

    import NuimoUUID._

    Future {
      serviceUUID match {
        case Characteristics.BUTTON_CLICK => handler.onClick(this, peripheralUUID, data)
        case Characteristics.ROTATION => handler.onRotate(this, peripheralUUID, data)
        case Characteristics.SWIPE => handler.onSwipe(this, peripheralUUID, data)
        case Characteristics.FLY => handler.onFly(this, peripheralUUID, data)
        case Characteristics.BATTERY => printBatteryStatus(peripheralUUID, data(0))
        case Events.CONNECTED => handler.onConnect(peripheralUUID)
        case Events.DISCONNECTED => handler.onDisconnect(peripheralUUID)
        case _ =>
      }
      lastEventTimeStamp = System.nanoTime()
    }
  }

  private def handlerIdStringForCurrentApp(peripheralUUID: String): String =
    uuid2config(peripheralUUID).handlerIDs.getOrElse(_appName, HandlerID.Default.toString)

  def printBatteryStatus(uuid: String, voltage: Int) = println( s"""Battery: $voltage %""")

  def writeLedImage(uuid: String, img: LedImage): Unit =
    writeLedImage(uuid, img.state, img.tag, img.brightness, img.duration)

  def writeLedImage(uuid: String, state: Seq[Int], tag: String = "", brightness: Int = 75, duration: Int = 10): Unit = {
    _imgTag = tag
    send(s"$uuid:led:${ state.mkString("") }:$brightness:$duration")
  }

  def requestBatteryStatus(uuid: String) = send(s"$uuid:battery")

  def send(data: String) = connection ! Write(ByteString(data))
}
