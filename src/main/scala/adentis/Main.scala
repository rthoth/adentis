package adentis

import adentis.module.DatabaseModule
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
    yield Exit.Success
