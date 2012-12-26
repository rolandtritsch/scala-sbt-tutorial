package sbt.tutorial

import sbt._
import sbt.Keys._
import sbt.KeyRanks._

object Build extends sbt.Build {
  // custom settings, custom tasks, ...
  val helloWorld = SettingKey[String]("hello-world", "sets a hello world string", BSetting)
  val showTime = TaskKey[String]("show-time", "shows the current time", BTask)
  val showTimeUpper = TaskKey[String]("show-time-upper", "shows the current time (in uppercase)", BTask)

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

  lazy val sub1 = Project("sub-project1", file("sub1"), settings = Project.defaultSettings ++ Seq(
    helloWorld := "Hello Roland (Project: sub-project1, Configuration: Global, Task: Global)",
    helloWorld in Compile := "Hello Roland (Project: sub-project1, Configuration: Compile, Task: Global)",
    helloWorld in (Compile, publish) := "Hello Roland (Project: sub-project1, Configuration: Compile, Task: publish)"
  ))

  lazy val sub2 = Project("sub-project2", file("sub2"), settings = Project.defaultSettings ++ Seq(
    helloWorld := "Hello Roland (Project: sub-project2, Configuration: Global, Task: Global)",
    helloWorld in (Compile, compile) := "Hello Roland (Project: sub-project2, Configuration: Compile, Task: compile)"
  )) dependsOn(util1)
}
