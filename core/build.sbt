name := "scalavro-core"

version := "0.1.0-SNAPSHOT"

resolvers ++= Seq(
  "Spray Repository" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray" % "spray-json_2.10.0-RC3" % "1.2.3"
)
