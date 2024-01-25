package adentis.module

import adentis.AdentisException
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import zio.Task
import zio.TaskLayer
import zio.ULayer
import zio.ZIO
import zio.ZLayer

object DatabaseModule {

  case class Config(
      username: String = "postgres",
      password: String = "adentis",
      database: String = "adentis",
      hostname: String = "localhost",
      port: Int = 5432
  )

  def default(): Task[DatabaseModule] = of(Config())

  def of(config: Config): Task[DatabaseModule] = ZIO.attempt {
    val dataSource = HikariDataSource()

    // In the real world it'd receive the whole URL.
    dataSource.setJdbcUrl(s"jdbc:postgresql://${config.hostname}:${config.port}/${config.database}")
    dataSource.setUsername(config.username)
    dataSource.setPassword(config.password)

    DatabaseModule(dataSource)
  }

}

class DatabaseModule(val dataSource: DataSource):

  def dataSourceLayer: ULayer[DataSource] = ZLayer.succeed(dataSource)

  def migrate(): Task[DatabaseModule] = ZIO
    .attemptBlocking {
      Flyway.configure().dataSource(dataSource).load().migrate()
      this
    }
    .mapError(AdentisException.Database("It was impossible to migrate the database!", _))
