ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "adentis",
    organization := "io.github.rthoth",
    version := "0.0.0-SNAPSHOT",
    isSnapshot := true,
    libraryDependencies ++= Seq(
      Dependencies.Zio
    ).flatten,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
