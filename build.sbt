organization := "com.gensler"

name := "scalavro"

version := "0.1.0-SNAPSHOT"

licenses in ThisBuild := Seq(
  "BSD-style" -> url("http://opensource.org/licenses/BSD-2-Clause")
)

scalaVersion := "2.10.1"

resolvers ++= Seq(
  "Spray Repository" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "io.spray" % "spray-json_2.10.0-RC3" % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
)

scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-feature")
