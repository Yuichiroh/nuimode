// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.handler

import yuima.nuimo.NuimoManager
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

  override def onSwipeLeft(uuid: String): Unit = doAction(pressAction, super.onSwipeLeft(uuid))

  override def onSwipeUp(uuid: String): Unit = doAction(swipeUpAction, super.onSwipeUp(uuid))

  override def onFlyRight(uuid: String): Unit = doAction(flyRightAction, super.onFlyRight(uuid))

  override def onFlyHover(uuid: String): Unit = doAction(flyHoverAction, super.onFlyHover(uuid))

  private def doAction(action: Option[Seq[SimpleNuimoAction]],
                       superAction: => Unit) = pressAction match {
    case Some(actions) => actions.foreach(a => NuimoManager.runAppleScript(a.script))
    case None => superAction
  }

  override def onRotateRight(uuid: String, velocity: Int): Unit = doAction(rotateRightAction, super.onRotateRight(uuid, velocity))

  override def onSwipeDown(uuid: String): Unit = doAction(swipeDownAction, super.onSwipeDown(uuid))

  override def onRotateLeft(uuid: String, velocity: Int): Unit = doAction(rotateLeftAction, super.onRotateLeft(uuid, velocity))

  override def onPress(uuid: String): Unit = doAction(pressAction, super.onPress(uuid))

  override def onSwipeRight(uuid: String): Unit = doAction(swipeRightAction, super.onSwipeRight(uuid))

  override def onRelease(uuid: String): Unit = doAction(releaseAction, super.onRelease(uuid))

  override def onFlyBackwards(uuid: String): Unit = doAction(flyBackwardsAction, super.onFlyBackwards(uuid))

  override def onFlyTowards(uuid: String): Unit = doAction(flyTowardsAction, super.onFlyTowards(uuid))

  override def onFlyLeft(uuid: String): Unit = doAction(flyLeftAction, super.onFlyLeft(uuid))
}
