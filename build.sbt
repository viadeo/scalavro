import scalariform.formatter.preferences._

organization in ThisBuild := "com.gensler"

name := "scalavro"

version in ThisBuild := "0.1.1-SNAPSHOT"

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

ScalariformKeys.preferences in ThisBuild := FormattingPreferences()
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
