// Copyright (c) 2016 Yuichiroh Matsubayashi

package yuima.nuimo.config

import yuima.nuimo.handler.{GoogleChromeHandler, LightroomHandler, PowerPointHandler}

/** An object defining mapping between the (string form) IDs of handlers and its actual instances.
  * The ids of handlers are used to specfiy a handler for each application in a config file.
  */
object Config {
  /** You need to define a mapping between handler's id and its actual instance. */
  val id2handler = Map(
    HandlerID.Default -> yuima.nuimo.defaultHandler,
    HandlerID.PowerPoint -> PowerPointHandler,
    HandlerID.Lightroom -> LightroomHandler,
    HandlerID.GoogleChrome -> GoogleChromeHandler
  )

  /** A class giving an ID for each nuimo handler in order to describe it on a config file. */
  object HandlerID extends Enumeration {
    val Default, PowerPoint, Lightroom, GoogleChrome = Value
  }

}

/** A class assigning handlers to particular applications on your nuimo.
  *
  * For peripheral UUID, iOS will not report actual hardware addresses to applications,
  * but rather reports a locally assigned temporary identifier which has no known mapping
  * back to the actual source address of the transmission, apart from the private data tables
  * maintained within the iOS bluetooth stack.
  * see: http://stackoverflow.com/questions/12524871/corebluetooth-how-to-get-a-unique-uuid
  *
  * The UUID is unique given the same pair iDevice-btDevice, as long as you do not flush the iOS Network cache
  * as mentioned here:
  * http://stackoverflow.com/questions/17575949/corebluetooth-what-is-the-lifetime-of-unique-uuids/17576559#17576559
  *
  * @param name     a specific name of the nuimo used for OSX notification.
  * @param handlers handlers for each application. A key is an application name (i.e., "Google Chrome.app") and
  *                 a value is a string of [[yuima.nuimo.config.Config.HandlerID]].
  * @param uuid     peripheral UUID assinged for a pair of (nuimo, your machine)
  */
case class NuimoConfig(uuid: String, name: String, handlers: Map[String, String])
