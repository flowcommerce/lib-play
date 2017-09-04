import play.sbt.PlayScala._

name := "lib-play"

organization := "io.flow"

scalaVersion in ThisBuild := "2.12.3"

crossScalaVersions := Seq("2.12.3")

version := "0.4.2-SNAPSHOT"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      filters,
      "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
      "com.github.ben-manes.caffeine" % "caffeine" % "2.5.5",
      "com.github.ben-manes.caffeine" % "guava" % "2.5.5",
      "com.ning" % "async-http-client" % "1.9.40",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1" % "test"
    ),
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    resolvers += "Artifactory" at "https://flow.artifactoryonline.com/flow/libs-release/",
    resolvers += "Artifactory-Snapshots-Local" at "https://flow.artifactoryonline.com/flow/libs-snapshot-local/",
    resolvers += "Artifactory-Releases-Local" at "https://flow.artifactoryonline.com/flow/libs-release-local/",
    credentials += Credentials(
      "Artifactory Realm",
      "flow.artifactoryonline.com",
      System.getenv("ARTIFACTORY_USERNAME"),
      System.getenv("ARTIFACTORY_PASSWORD")
    )
  )

publishTo := {
  val host = "https://flow.artifactoryonline.com/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}