// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import java.util.concurrent.Executors

import yuima.nuimo.action.SystemAction
import yuima.nuimo.config.Config
import yuima.nuimo.{NuimoEvent, Nuimode}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait NuimoHandler {
  val leftRotationSensitivity: Int
  val rightRotationSensitivity: Int
  val actionSpeed: Int

  private var isPressed = false
  private var actionInPressed = false
  private var lastValidActionTimeStamp = System.nanoTime()
  private var totalVelocity = 0
  private var click = 0

  final def onConnect(uuid: String) = {
    val name = Nuimode.uuid2config(uuid).name
    println(s"Connected!: $name")
    SystemAction.sendNotification(name, "Connected!")
  }

  final def onDisconnect(uuid: String) = {
    val name = Nuimode.uuid2config(uuid).name
    println(s"Disconnected: $name")
    SystemAction.sendNotification(name, "Disconnected.")
  }

  final def onClick(client: Nuimode, uuid: String, data: Array[Int]) = {
    val signal = data(0)
    val action = NuimoEvent.Click(signal)

    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(Config.numThreadPool))

    action match {
      case NuimoEvent.Click.PRESS =>
        if (Nuimode.hasSufficientEventInterval(Config.clickInterval.milli.toNanos)) // milli to nano
          click = 0
        click += 1
        isPressed = true
        onPress(client, uuid)
      case NuimoEvent.Click.RELEASE =>
        isPressed = false
        val currentClick = click
        val futureClick = Future {
          Thread.sleep(Config.clickInterval)
          click
        }
        if (currentClick == Await.result(futureClick, (Config.clickInterval + 20) milli)) {
          println(s"click: $currentClick on ${ Nuimode.appName }")
          click = 0
          if (!actionInPressed) onRelease(client, uuid, currentClick)
          else actionInPressed = false
        }
    }
  }

  def onPress(client: Nuimode, uuid: String): Unit

  def onRelease(client: Nuimode, uuid: String, clickCount: Int): Unit

  final def onSwipe(client: Nuimode, uuid: String, data: Array[Int]) = {
    val signal = data(0)
    val direction = NuimoEvent.Swipe(signal)

    direction match {
      case NuimoEvent.Swipe.LEFT => onSwipeLeft(client, uuid)
      case NuimoEvent.Swipe.RIGHT => onSwipeRight(client, uuid)
      case NuimoEvent.Swipe.UP => onSwipeUp(client, uuid)
      case NuimoEvent.Swipe.DOWN => onSwipeDown(client, uuid)
    }
  }

  def onSwipeLeft(client: Nuimode, uuid: String): Unit

  def onSwipeRight(client: Nuimode, uuid: String): Unit

  def onSwipeUp(client: Nuimode, uuid: String): Unit

  def onSwipeDown(client: Nuimode, uuid: String): Unit

  final def onRotate(client: Nuimode, uuid: String, data: Array[Int]) = {
    val signals = data.asInstanceOf[Array[Int]]
    val velocity = signals(0) - signals(1)
    val direction =
      if (signals(1) == 255) NuimoEvent.Rotate.LEFT
      else NuimoEvent.Rotate.RIGHT

    if (Nuimode.hasSufficientEventInterval) totalVelocity = 0
    totalVelocity += velocity

    direction match {
      case NuimoEvent.Rotate.LEFT =>
        val vel = totalVelocity / leftRotationSensitivity
        if (hasSufficientActionInterval && vel < 0) {
          if (isPressed) {
            onPressRotateLeft(client, uuid, vel)
            actionInPressed = true
          }
          else onRotateLeft(client, uuid, vel)
          totalVelocity %= leftRotationSensitivity
          lastValidActionTimeStamp = System.nanoTime()
        }
      case NuimoEvent.Rotate.RIGHT =>
        val vel = totalVelocity / rightRotationSensitivity
        if (hasSufficientActionInterval && vel > 0) {
          if (isPressed) {
            onPressRotateRight(client, uuid, vel)
            actionInPressed = true
          }
          else onRotateRight(client, uuid, vel)
          lastValidActionTimeStamp = System.nanoTime()
          totalVelocity %= rightRotationSensitivity
        }
    }
  }

  def hasSufficientActionInterval =
    System.nanoTime() - lastValidActionTimeStamp > Config.actionInterval.milli.toNanos / actionSpeed

  def onRotateLeft(client: Nuimode, uuid: String, velocity: Int): Unit

  def onRotateRight(client: Nuimode, uuid: String, velocity: Int): Unit

  def onPressRotateLeft(client: Nuimode, uuid: String, velocity: Int): Unit

  def onPressRotateRight(client: Nuimode, uuid: String, velocity: Int): Unit

  final def onFly(client: Nuimode, uuid: String, data: Array[Int]) = {
    val signal = data(0)
    val direction = NuimoEvent.Fly(signal)
    direction match {
      case NuimoEvent.Fly.LEFT => onFlyLeft(client, uuid)
      case NuimoEvent.Fly.RIGHT => onFlyRight(client, uuid)
      case NuimoEvent.Fly.BACKWARDS => onFlyBackwards(client, uuid)
      case NuimoEvent.Fly.TOWARDS => onFlyTowards(client, uuid)
      case NuimoEvent.Fly.HOVER =>
        val height = data(1)
        if (Nuimode.hasSufficientEventInterval)
          client.showBatteryStatus(uuid)
        onFlyHover(client, uuid, height)
    }
  }

  def onFlyLeft(client: Nuimode, uuid: String): Unit

  def onFlyRight(client: Nuimode, uuid: String): Unit

  def onFlyBackwards(client: Nuimode, uuid: String): Unit

  def onFlyTowards(client: Nuimode, uuid: String): Unit

  def onFlyHover(client: Nuimode, uuid: String, height: Int): Unit
}
