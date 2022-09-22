name := "lib-play-play28"

organization := "io.flow"

scalaVersion := "2.13.6"

lazy val allScalacOptions = Seq(
  "-feature",
  "-Xfatal-warnings",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Ypatmat-exhaust-depth", "100", // Fixes: Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
  "-Wconf:src=generated/.*:silent",
  "-Wconf:src=target/.*:silent", // silence the unused imports errors generated by the Play Routes
)

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      filters,
      guice,
      "io.flow" %% "lib-log" % "0.1.74",
      "com.pauldijou" %% "jwt-play-json" % "5.0.0",
      "com.ning" % "async-http-client" % "1.9.40",
      "org.apache.commons" % "commons-io" % "1.3.2",
      "org.mockito" % "mockito-core" % "4.8.0" % Test,
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.scalatestplus" %% "mockito-3-3" % "3.2.2.0" % Test,
    ),
    
    scalacOptions ++= allScalacOptions,
    // Suppresses problems with Scaladoc links
    Compile / doc / scalacOptions += "-no-link-warnings",
 
    Test / javaOptions += "-Dconfig.file=conf/test.conf",
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

version := "0.7.42"
