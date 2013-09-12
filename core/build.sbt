name := "scalavro-core"

resolvers += "Spray Micro-Repository" at "http://repo.spray.io/"

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.2.5",
  "org.apache.avro" % "avro" % "1.7.5"
)
