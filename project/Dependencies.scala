import sbt._

object Dependencies {

  val Zio = Seq(
    "dev.zio" %% "zio"          % Version.Zio,
    "dev.zio" %% "zio-streams"  % Version.Zio,
    "dev.zio" %% "zio-test"     % Version.Zio % Test,
    "dev.zio" %% "zio-test-sbt" % Version.Zio % Test
  )

  val ZQuill = Seq(
    "io.getquill" %% "quill-jdbc-zio" % Version.QuillJdbc
  )

  val Flyway = Seq(
    "org.flywaydb" % "flyway-core"                % Version.Flyway,
    "org.flywaydb" % "flyway-database-postgresql" % Version.Flyway
  )

  val Database = Seq(
    "com.zaxxer"     % "HikariCP"   % Version.HikariCP,
    "org.postgresql" % "postgresql" % Version.Postgres
  )

  val Ducktape = Seq(
    "io.github.arainko" %% "ducktape" % "0.1.11"
  )

  val ZioPostgresSQLTest = Seq(
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % "0.10.0" % Test
  )

  val Logging = Seq(
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.22.1"
  )

  object Version {
    val Zio       = "2.1-RC1"
    val QuillJdbc = "4.8.0"
    val Postgres  = "42.5.4"
    val Flyway    = "10.6.0"
    val HikariCP  = "5.1.0"
  }

}
