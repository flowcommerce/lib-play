import play.sbt.PlayScala._

name := "lib-play-play26"

organization := "io.flow"

scalaVersion in ThisBuild := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

version := "0.4.44"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      filters,
      guice,
      specs2 % Test,
      "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
      "com.ning" % "async-http-client" % "1.9.40",
      "io.flow" %% "lib-test-utils" % "0.0.6" % Test
    ),
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    resolvers += "Artifactory" at "https://flow.artifactoryonline.com/flow/libs-release/",
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
