addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")

addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.1.2")

// plugins for publishing to github pages follow:

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven" // broken?

addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "0.6.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.0")
