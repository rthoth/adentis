package adentis

import adentis.database.OrderQuerier
import adentis.module.DatabaseModule
import adentis.report.Reporter
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.Exit
import zio.Scope
import zio.ZIO
import zio.ZIOAppArgs
import zio.ZIOAppDefault

object Main extends ZIOAppDefault:

  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] =
    for
      args           <- ZIOAppArgs.getArgs
      commandLine    <- CommandLine(args)
      databaseModule <- DatabaseModule.default()
      _              <- databaseModule.migrate()
      orderQuerier   <- OrderQuerier.fromEnvironment
                          .provide(databaseModule.dataSourceLayer >>> Quill.Postgres.fromNamingStrategy(SnakeCase))
      foundOrder      = orderQuerier.byInterval(commandLine.query)
      _              <- Reporter
                          .report(foundOrder, commandLine.report)
                          .provide(LocalDateService.fixed(commandLine.query.ending))
    yield Exit.Success
