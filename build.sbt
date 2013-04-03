organization in ThisBuild := "com.gensler"

name := "scalavro"

version := "0.1.0-SNAPSHOT"

licenses in ThisBuild := Seq(
  "BSD-style" -> url("http://opensource.org/licenses/BSD-2-Clause")
)

scalaVersion in ThisBuild := "2.10.1"

libraryDependencies in ThisBuild ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.1",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
)

scalacOptions in (ThisBuild, Compile) ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

parallelExecution in (ThisBuild, Test) := false