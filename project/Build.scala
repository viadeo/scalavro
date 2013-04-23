import sbt._
import Keys._

import com.typesafe.sbt.SbtScalariform.scalariformSettings

object ScalavroBuild extends Build {

  lazy val root = Project(
    id = "scalavro",
    base = file(".")
  ) settings(scalariformSettings: _*) aggregate(
    core,
    io,
    util
  )

  lazy val core = Project(
    id = "core",
    base = file("core")
  ) settings(scalariformSettings: _*) dependsOn(
    util
  )

  lazy val io = Project(
    id = "io",
    base = file("io")
  ) settings(scalariformSettings: _*) dependsOn(
    core,
    util
  )

  lazy val util = Project(
    id = "util",
    base = file("util")
  ) settings(scalariformSettings: _*)

}
