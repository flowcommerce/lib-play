import play.PlayImport.PlayKeys._

name := "lib-play"

organization := "io.flow"

scalaVersion in ThisBuild := "2.11.8"

crossScalaVersions := Seq("2.11.8")

version := "0.1.6"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      "com.jason-goodwin" %% "authentikat-jwt" % "0.4.1",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "org.scalatestplus" %% "play" % "1.4.0" % "test"
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

