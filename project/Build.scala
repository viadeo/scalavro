import sbt._
import Keys._

import com.typesafe.sbt.SbtScalariform.scalariformSettings
import scalariform.formatter.preferences._

object ScalavroBuild extends Build {

  ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(IndentWithTabs, false)
    .setPreference(IndentSpaces, 2)
    .setPreference(DoubleIndentClassDeclaration, false)
    .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
    .setPreference(PreserveDanglingCloseParenthesis, false)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(SpaceBeforeColon, false)
    .setPreference(SpaceInsideBrackets, false)
    .setPreference(SpaceInsideParentheses, false)
    .setPreference(SpacesWithinPatternBinders, true)
    .setPreference(FormatXml, true)


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
