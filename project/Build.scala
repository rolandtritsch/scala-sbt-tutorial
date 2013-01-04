package sbt.tutorial

import sbt._
import sbt.Keys._
import sbt.KeyRanks._

import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._

object Build extends sbt.Build {
  // custom settings, custom tasks, ...
  val helloWorld = SettingKey[String]("hello-world", "sets a hello world string", BSetting)
  val showTime = TaskKey[String]("show-time", "shows the current time", BTask)
  val showTimeUpper = TaskKey[String]("show-time-upper", "shows the current time (in uppercase)", BTask)

  // creating a task for symetry (that means to make assembly look like the other package tasks)
  val packageAssembly = TaskKey[File]("package-assembly", "Produces the assembly artifact (by aliasing assembly)", BTask)

  // build level settings ...
  override lazy val settings = super.settings ++ Seq(
    helloWorld := "Hello Roland (Project: Global, Configuration: Global, Task: Global)",
    helloWorld in ThisBuild := "Hello Roland (Project: ThisBuild, Configuration: Global, Task: Global)",
    helloWorld in Compile := "Hello Roland (Project: Global, Configuration: Compile, Task: Global)",

    showTime := {(new java.util.Date).toString},
    showTimeUpper <<= showTime,
    showTimeUpper ~= {_.toUpperCase},

    name := "scala-sbt-tutorial",
    organization := "org.tritsch.scala",
    scalaVersion := "2.9.2"
  )

  // projects and sub-projects ...
  lazy val root = Project("root-project", file("."), settings = Project.defaultSettings ++ Seq(
  )) aggregate(sub1, sub2)

  lazy val util1 = Project("util-lib1", file("util1"), settings = Project.defaultSettings ++ Seq(
  ))

  // all off the assembly settings that are not (sub) project specific ...
  val myAssemblySettings = Seq(
    // alias package to packageAssembly and packageAssemply to assembly, means
    // you can now run sbt {package, package-bin, package-assembly, ...
    packageAssembly <<= assembly,
    Keys.`package` <<= packageAssembly,

    // disable running the tests, while building the assembly package (not
    // sure why this there in first place)
    test in assembly := {},

    // make the jarName match the format of the jar that is produced by package-bin (just for symetry) ...
    jarName in assembly <<= (name, scalaVersion, version) {(n, s, v) => n + "_" + s + "-" + v + "-assembly.jar"},

    // write the jar into the ./target/scala-<version>/... directory ... (just for symetry (because this
    // is where package-bin writes its jar files ...
    outputPath in assembly <<= (target, scalaVersion, jarName in assembly) {(t, s, j) => file(t + "/scala-" + s + "/" + j)},

    // create an Artifact and add it (see below) to make the publish work ...
    artifact in assembly <<= (name) {(n) => Artifact(name = n, `type` = "jar", extension = "jar", classifier = "assembly")},

    // make the MANIFEST look good (the Main-Class is added automatically) ...
    packageOptions += Package.ManifestAttributes(
      ("Manifest-Version", "1.0"),
      ("Created-By", "0.1 (Roland Tritsch, Inc)"),
      ("Signature-Version", "1.0"),
      ("Class-Path", "."),
      ("Foo", "Bar")
    )
  ) ++ addArtifact(artifact in assembly, assembly).settings

  lazy val sub1 = Project("sub-project1", file("sub1"), settings = Project.defaultSettings ++ assemblySettings ++ myAssemblySettings ++ Seq(
    helloWorld := "Hello Roland (Project: sub-project1, Configuration: Global, Task: Global)",
    helloWorld in Compile := "Hello Roland (Project: sub-project1, Configuration: Compile, Task: Global)",
    helloWorld in (Compile, publish) := "Hello Roland (Project: sub-project1, Configuration: Compile, Task: publish)"
  ))

  lazy val sub2 = Project("sub-project2", file("sub2"), settings = Project.defaultSettings ++ assemblySettings ++ myAssemblySettings ++ Seq(
    helloWorld := "Hello Roland (Project: sub-project2, Configuration: Global, Task: Global)",
    helloWorld in (Compile, compile) := "Hello Roland (Project: sub-project2, Configuration: Compile, Task: compile)"
  )) dependsOn(util1)
}
