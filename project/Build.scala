import sbt._
import Keys._

import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

object ScalavroBuild extends Build {

  lazy val root = Project(
    id = "scalavro",
    base = file("."),
    settings = commonSettings
  ) aggregate(
    core,
    io,
    util
  )

  lazy val core = Project(
    id = "core",
    base = file("core"),
    settings = commonSettings
  ) dependsOn(
    util
  )

  lazy val io = Project(
    id = "io",
    base = file("io"),
    settings = commonSettings
  ) dependsOn(
    core,
    util
  )

  lazy val util = Project(
    id = "util",
    base = file("util"),
    settings = commonSettings
  )

  lazy val commonSettings = Project.defaultSettings ++ scalariformSettings ++ customFormatSettings

  def customFormatSettings = Seq(
    ScalariformKeys.preferences := FormattingPreferences()
      .setPreference(IndentWithTabs, false)
      .setPreference(IndentSpaces, 2)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(PreserveSpaceBeforeArguments, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)
      .setPreference(FormatXml, true)

  )

}
