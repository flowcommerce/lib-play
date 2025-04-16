name := "lib-play-play29"
organization := "io.flow"

scalaVersion := "2.13.15"
ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

// Resolve scala-xml version dependency mismatch, see https://github.com/sbt/sbt/issues/7007
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
)

enablePlugins(GitVersioning)
git.useGitDescribe := true

coverageExcludedFiles := ".*\\/app/generated\\/.*"
coverageDataDir := file("target/scala-2.13")
coverageHighlighting := true
coverageFailOnMinimum := true
coverageMinimumStmtTotal := 58
coverageMinimumBranchTotal := 58

lazy val allScalacOptions = Seq(
  "-feature",
  "-Xfatal-warnings",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Ypatmat-exhaust-depth",
  "100", // Fixes: Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
  "-Wconf:src=generated/.*:silent",
  "-Wconf:src=target/.*:silent", // silence the unused imports errors generated by the Play Routes
)

val akkaVersion = play.core.PlayVersion.akkaVersion

lazy val root = project
  .in(file("."))
  .enablePlugins(PlayScala)
  .settings(
    scalafmtOnCompile := true,
    libraryDependencies ++= Seq(
      ws,
      filters,
      guice,
      "io.flow" %% "lib-log-play29" % "0.2.40",
      "io.flow" %% "lib-akka-play29" % "0.2.51",
      "com.pauldijou" %% "jwt-play-json" % "5.0.0",
      "javax.inject" % "javax.inject" % "1",
      "org.apache.commons" % "commons-io" % "1.3.2",
      "org.typelevel" %% "cats-core" % "2.10.0",
      "org.mockito" % "mockito-core" % "4.11.0" % Test,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "org.scalatestplus" %% "mockito-3-3" % "3.2.2.0" % Test,
    ),
    scalacOptions ++= allScalacOptions ++ Seq("-release", "17"),
    // Suppresses problems with Scaladoc links
    Compile / doc / scalacOptions += "-no-link-warnings",
    Test / javaOptions += "-Dconfig.file=conf/test.conf",
    Test / javaOptions ++= Seq(
      "--add-exports=java.base/sun.security.x509=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED",
    ),
    resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/",
    credentials += Credentials(
      "Artifactory Realm",
      "flow.jfrog.io",
      System.getenv("ARTIFACTORY_USERNAME"),
      System.getenv("ARTIFACTORY_PASSWORD"),
    ),
  )

publishTo := {
  val host = "https://flow.jfrog.io/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}
