// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.action.SystemAction
import yuima.nuimo.{Client, NuimoEvent, Nuimode}

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

  final def onClick(client: Client, uuid: String, data: Any) = {
    val signal = data.asInstanceOf[Array[Int]](0)
    val action = NuimoEvent.Click(signal)

    action match {
      case NuimoEvent.Click.PRESS =>
        isPressed = true
        onPress(client, uuid)
      case NuimoEvent.Click.RELEASE =>
        println(Nuimode.appName)
        isPressed = false
        if (!actionInPressed) onRelease(client, uuid)
        else actionInPressed = false
    }
  }

  def onPress(client: Client, uuid: String): Unit

  def onRelease(client: Client, uuid: String): Unit

  final def onSwipe(client: Client, uuid: String, data: Any) = {
    val signal = data.asInstanceOf[Array[Int]](0)
    val direction = NuimoEvent.Swipe(signal)

    direction match {
      case NuimoEvent.Swipe.LEFT => onSwipeLeft(client, uuid)
      case NuimoEvent.Swipe.RIGHT => onSwipeRight(client, uuid)
      case NuimoEvent.Swipe.UP => onSwipeUp(client, uuid)
      case NuimoEvent.Swipe.DOWN => onSwipeDown(client, uuid)
    }
  }

  def onSwipeLeft(client: Client, uuid: String): Unit

  def onSwipeRight(client: Client, uuid: String): Unit

  def onSwipeUp(client: Client, uuid: String): Unit

  def onSwipeDown(client: Client, uuid: String): Unit

  final def onRotate(client: Client, uuid: String, data: Any) = {
    val signals = data.asInstanceOf[Array[Int]].toArray
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
    System.nanoTime() - lastValidActionTimeStamp > Nuimode.actionInterval / actionSpeed

  def onRotateLeft(client: Client, uuid: String, velocity: Int): Unit

  def onRotateRight(client: Client, uuid: String, velocity: Int): Unit

  def onPressRotateLeft(client: Client, uuid: String, velocity: Int): Unit

  def onPressRotateRight(client: Client, uuid: String, velocity: Int): Unit

  final def onFly(client: Client, uuid: String, data: Any) = {
    val signal = data.asInstanceOf[Array[Int]](0)
    val direction = NuimoEvent.Fly(signal)
    direction match {
      case NuimoEvent.Fly.LEFT => onFlyLeft(client, uuid)
      case NuimoEvent.Fly.RIGHT => onFlyRight(client, uuid)
      case NuimoEvent.Fly.BACKWARDS => onFlyBackwards(client, uuid)
      case NuimoEvent.Fly.TOWARDS => onFlyTowards(client, uuid)
      case NuimoEvent.Fly.HOVER =>
        if (Nuimode.hasSufficientEventInterval)
          client.showBatteryStatus(uuid)
        onFlyHover(client, uuid)
    }
  }

  def onFlyLeft(client: Client, uuid: String): Unit

  def onFlyRight(client: Client, uuid: String): Unit

  def onFlyBackwards(client: Client, uuid: String): Unit

  def onFlyTowards(client: Client, uuid: String): Unit

  def onFlyHover(client: Client, uuid: String): Unit
}
