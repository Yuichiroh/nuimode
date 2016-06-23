// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.action.SystemAction
import yuima.nuimo.{NuimoEvent, NuimoManager}

import scala.scalajs.js

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/16.
  */
trait NuimoHandler {
  val leftRotationSensitivity: Int
  val rightRotationSensitivity: Int

  def onConnect(uuid: String) = {
    val name = NuimoManager.uuid2config(uuid).name
    println(s"Connected!: $name")
    SystemAction.sendNotification(name, "Connected!")
  }

  def onDisconnect(uuid: String) = {
    val name = NuimoManager.uuid2config(uuid).name
    println(s"Disconnected: $name")
    SystemAction.sendNotification(name, "Disconnected.")
  }

  def onSwipe(uuid: String, data: Any) = {
    val signal = data.asInstanceOf[js.Array[Int]](0)
    val direction = NuimoEvent.Swipe(signal)

    direction match {
      case NuimoEvent.Swipe.LEFT => onSwipeLeft(uuid)
      case NuimoEvent.Swipe.RIGHT => onSwipeRight(uuid)
      case NuimoEvent.Swipe.UP => onSwipeUp(uuid)
      case NuimoEvent.Swipe.DOWN => onSwipeDown(uuid)
    }
  }

  def onSwipeLeft(uuid: String): Unit

  def onSwipeRight(uuid: String): Unit

  def onSwipeUp(uuid: String): Unit

  def onSwipeDown(uuid: String): Unit

  def onClick(uuid: String, data: Any) = {
    val signal = data.asInstanceOf[js.Array[Int]](0)
    val action = NuimoEvent.Click(signal)

    action match {
      case NuimoEvent.Click.PRESS => onPress(uuid)
      case NuimoEvent.Click.RELEASE =>
        println(NuimoManager.appName)
        onRelease(uuid)
    }
  }

  def onPress(uuid: String): Unit

  def onRelease(uuid: String): Unit

  def onRotate(uuid: String, data: Any) = {
    val signals = data.asInstanceOf[js.Array[Int]].toArray
    val direction =
      if (signals(1) == 255) NuimoEvent.Rotate.LEFT
      else NuimoEvent.Rotate.RIGHT
    val velocity = signals(0) - signals(1)

    direction match {
      case NuimoEvent.Rotate.LEFT => onRotateLeft(uuid, velocity)
      case NuimoEvent.Rotate.RIGHT => onRotateRight(uuid, velocity)
    }
  }

  def onRotateLeft(uuid: String, velocity: Int): Unit

  def onRotateRight(uuid: String, velocity: Int): Unit

  def onFly(uuid: String, data: Any) = {
    val signal = data.asInstanceOf[js.Array[Int]](0)
    val direction = NuimoEvent.Fly(signal)
    direction match {
      case NuimoEvent.Fly.LEFT => onFlyLeft(uuid)
      case NuimoEvent.Fly.RIGHT => onFlyRight(uuid)
      case NuimoEvent.Fly.BACKWARDS => onFlyBackwards(uuid)
      case NuimoEvent.Fly.TOWARDS => onFlyTowards(uuid)
      case NuimoEvent.Fly.HOVER =>
        if (NuimoManager.hasSufficientEventInterval)
          NuimoManager.showBatteryStatus(uuid)
        onFlyHover(uuid)
    }
  }

  def onFlyLeft(uuid: String): Unit

  def onFlyRight(uuid: String): Unit

  def onFlyBackwards(uuid: String): Unit

  def onFlyTowards(uuid: String): Unit

  def onFlyHover(uuid: String): Unit
}
