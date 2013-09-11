import scalariform.formatter.preferences._

organization in ThisBuild := "com.gensler"

name := "scalavro"

version in ThisBuild := "0.3.2-class-union-erasure-workaround-SNAPSHOT"

libraryDependencies in ThisBuild ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.2",
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
)

scalacOptions in (ThisBuild, Compile) ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

parallelExecution in (ThisBuild, Test) := false

fork in (ThisBuild, Test) := true

// publish settings

publishMavenStyle in ThisBuild := true

useGpg in ThisBuild := true

pomIncludeRepository in ThisBuild := { _ => false }

licenses in ThisBuild := Seq(
  "BSD-style" -> url("http://opensource.org/licenses/BSD-2-Clause")
)

homepage in ThisBuild := Some(url("http://genslerappspod.github.io/scalavro/"))

scalaVersion in ThisBuild := "2.10.2"

publishTo in ThisBuild <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra in ThisBuild := (
  <scm>
    <url>git@github.com:GenslerAppsPod/scalavro.git</url>
    <connection>scm:git:git@github.com:GenslerAppsPod/scalavro.git</connection>
  </scm>
  <developers>
    <developer>
      <id>ConnorDoyle</id>
      <name>Connor Doyle</name>
      <url>http://gensler.com</url>
    </developer>
  </developers>
)

// formatter settings

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
