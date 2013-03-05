name := "scalavro"

organization := "com.gensler"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
)

scalacOptions in Compile ++= Seq("-feature")