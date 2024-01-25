import sbt._

object Dependencies {

  val Zio = Seq(
    "dev.zio" %% "zio"          % Version.Zio,
    "dev.zio" %% "zio-streams"  % Version.Zio,
    "dev.zio" %% "zio-test"     % Version.Zio % Test,
    "dev.zio" %% "zio-test-sbt" % Version.Zio % Test
  )

  object Version {
    val Zio = "2.1-RC1"
  }

}
