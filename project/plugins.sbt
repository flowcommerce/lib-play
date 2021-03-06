// Comment to get more information during initialization
logLevel := Level.Warn

// Artifactory credentials
credentials += Credentials(Path.userHome / ".ivy2" / ".artifactory")

// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release-local/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.5")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")

resolvers += "Flow Plugins" at "https://flow.jfrog.io/flow/plugins-release/"

addSbtPlugin("io.flow" % "sbt-flow-linter" % "0.0.33")
