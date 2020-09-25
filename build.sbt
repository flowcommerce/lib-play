name := "lib-play-play28"

organization := "io.flow"

scalaVersion := "2.13.3"

version := "0.6.13"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      filters,
      guice,
      "io.flow" %% "lib-log" % "0.1.20",
      "com.pauldijou" %% "jwt-play-json" % "4.3.0",
      "com.ning" % "async-http-client" % "1.9.40",
      "org.apache.commons" % "commons-io" % "1.3.2",
      "org.mockito" % "mockito-core" % "3.5.11" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.scalatestplus" %% "mockito-3-3" % "3.2.0.0" % Test,
      compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.7.0" cross CrossVersion.full),
      "com.github.ghik" %% "silencer-lib" % "1.7.0" % Provided cross CrossVersion.full,
    ),
    
    // silence all warnings on autogenerated files
    flowGeneratedFiles ++= Seq(
      "app/generated/.*".r,
    ),
    // Make sure you only exclude warnings for the project directories, i.e. make builds reproducible
    scalacOptions += s"-P:silencer:sourceRoots=${baseDirectory.value.getCanonicalPath}",
    // Suppresses problems with Scaladoc links
    scalacOptions in (Compile, doc) += "-no-link-warnings",
 
    javaOptions in Test += "-Dconfig.file=conf/test.conf",
    resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/",
    credentials += Credentials(
      "Artifactory Realm",
      "flow.jfrog.io",
      System.getenv("ARTIFACTORY_USERNAME"),
      System.getenv("ARTIFACTORY_PASSWORD")
    )
  )

publishTo := {
  val host = "https://flow.jfrog.io/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}

