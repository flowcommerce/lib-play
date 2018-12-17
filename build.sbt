import play.sbt.PlayScala._

name := "lib-play-play26"

organization := "io.flow"

scalaVersion in ThisBuild := "2.12.8"

version := "0.5.17"

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      ws,
      filters,
      guice,
      "io.flow" %% "lib-util" % "0.1.2",
      "io.flow" %% "lib-akka" % "0.0.3",
      "io.flow" %% "lib-log" % "0.0.45",
      "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
      "com.ning" % "async-http-client" % "1.9.40",
      "org.apache.commons" % "commons-io" % "1.3.2",
      // The following libs are Provided so dependencies are only included if io.flow.play.actors.proxy.* is used
      "com.typesafe.akka" %% "akka-stream" % "2.5.18" % Provided,
      "com.typesafe.akka" %% "akka-slf4j" % "2.5.13" % Provided,
      "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % "0.20" % Provided,
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "net.logstash.logback" % "logstash-logback-encoder" % "5.2",
      specs2 % Test
    ),
    javaOptions in Test += "-Dconfig.file=conf/test.conf",
    resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
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
