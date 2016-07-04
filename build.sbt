name := "Nuimode"

version := "1.1.2"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.7",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.7",
  "com.typesafe.akka" %% "akka-stream" % "2.4.7",
  "com.lihaoyi" %% "upickle" % "0.4.1"
)