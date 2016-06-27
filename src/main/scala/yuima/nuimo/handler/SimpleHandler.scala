// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.{Client, Nuimode}
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

  override def onSwipeLeft(client:Client, uuid: String): Unit = doAction(pressAction, super.onSwipeLeft(client, uuid))

  override def onSwipeUp(client:Client, uuid: String): Unit = doAction(swipeUpAction, super.onSwipeUp(client, uuid))

  override def onFlyRight(client:Client, uuid: String): Unit = doAction(flyRightAction, super.onFlyRight(client, uuid))

  override def onFlyHover(client:Client, uuid: String): Unit = doAction(flyHoverAction, super.onFlyHover(client, uuid))

  private def doAction(action: Option[Seq[SimpleNuimoAction]],
                       superAction: => Unit) = pressAction match {
    case Some(actions) => actions.foreach(a => Nuimode.runAppleScript(a.script))
    case None => superAction
  }

  override def onRotateRight(client:Client, uuid: String, velocity: Int): Unit = doAction(rotateRightAction, super.onRotateRight(client, uuid, velocity))

  override def onSwipeDown(client:Client, uuid: String): Unit = doAction(swipeDownAction, super.onSwipeDown(client, uuid))

  override def onRotateLeft(client:Client, uuid: String, velocity: Int): Unit = doAction(rotateLeftAction, super.onRotateLeft(client, uuid, velocity))

  override def onPress(client:Client, uuid: String): Unit = doAction(pressAction, super.onPress(client, uuid))

  override def onSwipeRight(client:Client, uuid: String): Unit = doAction(swipeRightAction, super.onSwipeRight(client, uuid))

  override def onRelease(client:Client, uuid: String): Unit = doAction(releaseAction, super.onRelease(client, uuid))

  override def onFlyBackwards(client:Client, uuid: String): Unit = doAction(flyBackwardsAction, super.onFlyBackwards(client, uuid))

  override def onFlyTowards(client:Client, uuid: String): Unit = doAction(flyTowardsAction, super.onFlyTowards(client, uuid))

  override def onFlyLeft(client:Client, uuid: String): Unit = doAction(flyLeftAction, super.onFlyLeft(client, uuid))
}
