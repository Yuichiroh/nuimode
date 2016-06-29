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

  val numThreadPool = 4
  val actionInterval = 200
  val clickInterval = 200
}
