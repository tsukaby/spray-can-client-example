name := "spray-can-client-example"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "io.spray" %% "spray-can" % "1.3.3", // Http API. Depended on akka-actor.
  "io.spray" %% "spray-httpx" % "1.3.3", // For marshaling
  "io.spray" %% "spray-client" % "1.3.3" // High level API over the spray-can.
)
