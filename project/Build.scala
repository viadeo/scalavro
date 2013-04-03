import sbt._
import Keys._

object ScalavroBuild extends Build {

  lazy val root = Project(id = "scalavro", base = file(".")) aggregate(
    core,
    io,
    util
  )

  lazy val core = Project(
    id = "core",
    base = file("core")
  ) dependsOn(
    util
  )

  lazy val io = Project(
    id = "io",
    base = file("io")
  ) dependsOn(
    core,
    util
  )

  lazy val util = Project(
    id = "util",
    base = file("util")
  )

}
