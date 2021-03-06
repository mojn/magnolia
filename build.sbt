import com.typesafe.sbt.pgp.PgpKeys.publishSigned
import sbt.Keys.crossScalaVersions

lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.11"
lazy val supportedScalaVersions = List(scala211,scala212)

ThisBuild / scalaVersion := scala211

crossScalaVersions := supportedScalaVersions
releaseCrossBuild := true

ThisBuild / turbo := true

lazy val core = (project in file("core/shared"))
  .settings(buildSettings: _*)
  .settings(publishSettings: _*)
  .settings(scalaMacroDependencies: _*)
  .settings(moduleName := "magnolia")
  .settings(
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      "com.propensive" %% "mercator" % "0.1.1"
    ),
    credentials += Credentials(Path.userHome / ".sbt" / "liveintent.jfrog.io.credentials") )
  .settings(
    crossScalaVersions := supportedScalaVersions
  )
//  .jvmSettings(
//    crossScalaVersions := "2.12.9" :: "2.11.12" :: Nil
//  )
//  .jsSettings(
//    crossScalaVersions := "2.12.9" :: "2.11.12" :: Nil
//  )
//  .nativeSettings(
//    crossScalaVersions := "2.11.12" :: Nil
//  )

//lazy val coreJVM = core.jvm
//lazy val coreJS = core.js
//lazy val coreNative = core.native

//lazy val examples = crossProject(JVMPlatform)
//  .in(file("examples"))
//  .settings(buildSettings: _*)
//  .settings(publishSettings: _*)
//  .settings(moduleName := "magnolia-examples")
//  .jvmSettings(
//    crossScalaVersions := (crossScalaVersions in coreJVM).value,
//    scalaVersion := (scalaVersion in coreJVM).value
//  )
//  .jsSettings(
//    crossScalaVersions := (crossScalaVersions in coreJS).value,
//    scalaVersion := (scalaVersion in coreJS).value
//  )
//  .nativeSettings(
//    crossScalaVersions := (crossScalaVersions in coreNative).value,
//    scalaVersion := (scalaVersion in coreNative).value
//  )
//  .dependsOn(core)
//
//lazy val examplesJVM = examples.jvm
//lazy val examplesJS = examples.js
//lazy val examplesNative = examples.native

//lazy val tests = project
//  .in(file("tests"))
//  .settings(buildSettings: _*)
//  .settings(unmanagedSettings)
//  .settings(moduleName := "magnolia-tests")
//  .settings(
//    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
//    initialCommands in console := """import magnolia.tests._; import magnolia.examples._;""",
//    libraryDependencies ++= Seq(
      // These two to allow compilation under Java 9...
      // Specifically to import XML stuff that got modularised
//      "javax.xml.bind" % "jaxb-api" % "2.3.0" % "compile",
//      "com.sun.xml.bind" % "jaxb-impl" % "2.3.0" % "compile"
//    )
//  )
//  .dependsOn(examplesJVM)

lazy val root = (project in file("."))
//  .aggregate(coreJVM, coreJS, coreNative, examplesJVM, examplesJS, examplesNative, tests)
  .aggregate(core)
  .settings(
    publish := {},
    publishLocal := {}
  )

//lazy val benchmarks = project
//  .in(file("benchmarks"))
//  .settings(buildSettings: _*)
//  .settings(moduleName := "magnolia-benchmarks")
//  .dependsOn(examplesJVM)

lazy val buildSettings = Seq(
  organization := "com.propensive",
  name := "magnolia",
  version := "0.10.0-li",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Xfuture",
    "-Ywarn-value-discard",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
  ),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, v)) if v <= 12 =>
        Seq(
          "-Xexperimental",
          "-Ywarn-nullary-unit",
          "-Ywarn-inaccessible",
          "-Ywarn-adapted-args"
        )
      case _ =>
        Nil
    }
  },
  scmInfo := Some(
    ScmInfo(url("https://github.com/propensive/magnolia"),
            "scm:git:git@github.com:propensive/magnolia.git")
  )
)

lazy val publishSettings = Seq(
  homepage := Some(url("http://magnolia.work/")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  autoAPIMappings := true,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  publishTo := {
    val jfrog = "https://liveintent.jfrog.io/artifactory/berlin"
    if (isSnapshot.value)
      Some("snapshots" at jfrog + ";build.timestamp=" + new java.util.Date().getTime)
    else
      Some("releases"  at jfrog)
  },
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  pomExtra := (
    <developers>
      <developer>
        <id>propensive</id>
        <name>Jon Pretty</name>
        <url>https://github.com/propensive/magnolia/</url>
      </developer>
    </developers>
  )
)

lazy val unmanagedSettings = unmanagedBase :=
  baseDirectory.value / "lib" /
    (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) => "2.11"
      case _             => "2.12"
    })

lazy val scalaMacroDependencies: Seq[Setting[_]] = Seq(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
)
