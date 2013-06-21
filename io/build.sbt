name := "scalavro-io"

resolvers ++= Seq(
  "Spray Repository" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "org.apache.avro" % "avro" % "1.7.4"
)
