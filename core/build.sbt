name := "scalavro-core"

resolvers ++= Seq(
  "Spray Repository" at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.2.5"
)
