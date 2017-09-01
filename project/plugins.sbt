// Comment to get more information during initialization
logLevel := Level.Warn

// Artifactory credentials
credentials += Credentials(Path.userHome / ".ivy2" / ".artifactory")

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Artifactory" at "https://flow.artifactoryonline.com/flow/libs-release-local/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")