import sbt._
import Keys._

object ScalavroBuild extends Build {

  lazy val root = Project(id = "models", base = file(".")) aggregate(
    core,
    io
  )

  lazy val core = Project(
    id = "core",
    base = file("core")
  )

  lazy val io = Project(
    id = "io",
    base = file("io")
  ) dependsOn(
    core
  )

}
