// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.Nuimode
import yuima.nuimo.action.SimpleNuimoAction

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/06/17.
  */
trait SimpleHandler extends DefaultHandler {
  val pressAction: Option[Seq[SimpleNuimoAction]] = None
  val releaseAction: Option[Seq[SimpleNuimoAction]] = None
  val swipeLeftAction: Option[Seq[SimpleNuimoAction]] = None
  val swipeRightAction: Option[Seq[SimpleNuimoAction]] = None
  val swipeUpAction: Option[Seq[SimpleNuimoAction]] = None
  val swipeDownAction: Option[Seq[SimpleNuimoAction]] = None
  val rotateLeftAction: Option[Seq[SimpleNuimoAction]] = None
  val rotateRightAction: Option[Seq[SimpleNuimoAction]] = None
  val flyLeftAction: Option[Seq[SimpleNuimoAction]] = None
  val flyRightAction: Option[Seq[SimpleNuimoAction]] = None
  val flyTowardsAction: Option[Seq[SimpleNuimoAction]] = None
  val flyBackwardsAction: Option[Seq[SimpleNuimoAction]] = None
  val flyHoverAction: Option[Seq[SimpleNuimoAction]] = None

  override def onSwipeLeft(client: Nuimode, uuid: String): Unit =
    doAction(pressAction, super.onSwipeLeft(client, uuid))

  override def onSwipeUp(client: Nuimode, uuid: String): Unit =
    doAction(swipeUpAction, super.onSwipeUp(client, uuid))

  override def onFlyRight(client: Nuimode, uuid: String): Unit =
    doAction(flyRightAction, super.onFlyRight(client, uuid))

  override def onFlyHover(client: Nuimode, uuid: String): Unit =
    doAction(flyHoverAction, super.onFlyHover(client, uuid))

  override def onRotateRight(client: Nuimode, uuid: String, velocity: Int): Unit =
    doAction(rotateRightAction, super.onRotateRight(client, uuid, velocity))

  override def onSwipeDown(client: Nuimode, uuid: String): Unit =
    doAction(swipeDownAction, super.onSwipeDown(client, uuid))

  override def onRotateLeft(client: Nuimode, uuid: String, velocity: Int): Unit =
    doAction(rotateLeftAction, super.onRotateLeft(client, uuid, velocity))

  private def doAction(action: Option[Seq[SimpleNuimoAction]], superAction: => Unit) = pressAction match {
    case Some(actions) => actions.foreach(a => Nuimode.runAppleScript(a.script))
    case None => superAction
  }

  override def onPress(client: Nuimode, uuid: String): Unit =
    doAction(pressAction, super.onPress(client, uuid))

  override def onSwipeRight(client: Nuimode, uuid: String): Unit =
    doAction(swipeRightAction, super.onSwipeRight(client, uuid))

  override def onRelease(client: Nuimode, uuid: String, clickCount: Int): Unit =
    doAction(releaseAction, super.onRelease(client, uuid, clickCount))

  override def onFlyBackwards(client: Nuimode, uuid: String): Unit =
    doAction(flyBackwardsAction, super.onFlyBackwards(client, uuid))

  override def onFlyTowards(client: Nuimode, uuid: String): Unit =
    doAction(flyTowardsAction, super.onFlyTowards(client, uuid))

  override def onFlyLeft(client: Nuimode, uuid: String): Unit =
    doAction(flyLeftAction, super.onFlyLeft(client, uuid))
}
