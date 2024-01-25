package adentis

import adentis.module.DatabaseModule
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import io.github.scottweaver.zillen.Docker
import io.github.scottweaver.zio.testcontainers.postgresql.PostgresContainer
import javax.sql.DataSource
import zio.RLayer
import zio.Scope
import zio.ZIO
import zio.ZLayer

trait DatabaseSpec extends AdentisSpec:

  protected def dataSourceLayer: RLayer[Scope, DataSource] =
    (Docker.layer() ++ PostgresContainer.Settings
      .default(builder = _.copy(imageVersion = "16-alpine"))) >>> PostgresContainer.layer

  protected def postgresLayerWithMigrate: RLayer[Scope, Quill.Postgres[SnakeCase]] =
    dataSourceLayer >>> ZLayer {
      for
        dataSource <- ZIO.service[DataSource]
        _          <- new DatabaseModule(dataSource).migrate()
      yield dataSource
    } >>> Quill.Postgres.fromNamingStrategy(SnakeCase)

  protected def postgresLayerWithoutMigrate: RLayer[Scope, Quill.Postgres[SnakeCase]] =
    dataSourceLayer >>> Quill.Postgres.fromNamingStrategy(SnakeCase)
