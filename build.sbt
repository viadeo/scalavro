name := "scalavro"

organization := "com.gensler"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.0"
)

scalacOptions in Compile ++= Seq("-feature")