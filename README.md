# Nuimode

A Scala library for customizing Nuimo devices on OSX.

You can implement your own event handler for each particular application by extending `NuimoHandler` trait.
The library provides some useful functions for handling Nuimo.

* Reading battery information
* Typing keystrokes
* Executing applescripts
* Handling events of a (multiple-) click, swipes, rotations, and fly actions (incl. height info.)
* Displaying an LED image

The library also bundles some example handlers:
* Volume controller ([DefaultHandler](https://github.com/Yuichiroh/nuimode/blob/master/src/main/scala/yuima/nuimo/handler/DefaultHandler.scala))
* iTunes controller ([DefaultHandler](https://github.com/Yuichiroh/nuimode/blob/master/src/main/scala/yuima/nuimo/handler/DefaultHandler.scala))
* Google chrome YouTube controller ([GoogleChromeHandler](https://github.com/Yuichiroh/nuimode/blob/master/src/main/scala/yuima/nuimo/handler/GoogleChromeHandler.scala))
* Adobe Lightroom controller ([LightroomHandler](https://github.com/Yuichiroh/nuimode/blob/master/src/main/scala/yuima/nuimo/handler/LightroomHandler.scala))
* Microsoft Power Point controller ([PowerPointHandler](https://github.com/Yuichiroh/nuimode/blob/master/src/main/scala/yuima/nuimo/handler/PowerPointHandler.scala))

# Setup
1. Install
    * [A recent version of Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
    * [sbt](http://www.scala-sbt.org/)
    * [node.js](https://nodejs.org/en/)
    * Xcode
1. Then, run ```npm install noble```
to install [noble: A Node.js BLE (Bluetooth Low Energy)](https://github.com/sandeepmistry/noble) library.
1. Clone this library.

# Usage
On your console,

1. ```cd /path/to/nuimode```
1. ```cp config/nuimo_config_example.txt config/nuimo_config.txt```
1. Run ```sbt run```
1. Turn on your Nuimo.
1. Confirm the UUID of your Nuimo on the console.
1. Paste the UUID on `config/nuimo_config.txt`
1. Type ```Ctrl-C``` to stop Nuimode
1. Run ```sbt run``` again.

# Advanced Configuration

If you want to implement your own handler, see `yuima.nuimo.handler.NuimoHandler` and `yuima.nuimo.handler.DeafaultHander` for example.

In order to use your handler, you have to define a mapping between an application's name and the handler by modifying `config/nuimo_config.txt` and `yuima.nuimo.config.Config` object.

For example, if you create a new object `SafariHandler`, you have to edit `yuima.nuimo.config.Config.HandlerID` as:
```Scala
object HandlerID extends Enumeration {
  val Default, PowerPoint, Lightroom, GoogleChrome, Safari = Value
}
```
Then you modify `yuima.nuimo.config.Config.id2handler` as:
```Scala
val id2handler = Map(
   HandlerID.Default -> yuima.nuimo.defaultHandler,
   HandlerID.PowerPoint -> PowerPointHandler,
   HandlerID.Lightroom -> LightroomHandler,
   HandlerID.GoogleChrome -> GoogleChromeHandler,
   HandlerID.Safari -> SafariHandler
)
```
Finally, you assign the application name to the handler on `config/nuimo_config.txt` as:
```json
[
  {
    "uuid": "123qweasdzxc456rtyfghvbn789uiojk",
    "name": "Left-hand Nuimo on MBA",
    "handlers": {
      "Microsoft PowerPoint.app": "PowerPoint",
      "Adobe Lightroom.app": "Lightroom",
      "Google Chrome.app": "GoogleChrome",
      "Safari.app": "Safari"
    }
  }
]
```

# Acknowledgement
Some JS scripts used in the library are forked from the following projects.

* https://github.com/brendonparker/nuimo-node-demo
* https://github.com/TooTallNate/node-applescript
