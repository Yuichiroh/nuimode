enablePlugins(ScalaJSPlugin)

name := "NuimoManager"

version := "1.0"

scalaVersion := "2.11.8"

scalaJSUseRhino in Global := false

scalaJSStage in Global := FullOptStage

//scalaJSStage in Global := FastOptStage

//libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"

//libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.0"

libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.1"

skip in packageJSDependencies := false

jsDependencies += ProvidedJS / "js/io.js"

jsDependencies += ProvidedJS / "js/nuimo.js"

jsDependencies += ProvidedJS / "js/applescript.js"

jsDependencies += ProvidedJS / "js/applescript-parser.js"

//jsDependencies += "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"

//jsDependencies += RuntimeDOM

//libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test"

//testFrameworks += new TestFramework("utest.runner.Framework")

persistLauncher in Compile := false

persistLauncher in Test := false
