package adentis

import adentis.database.OrderQuerier
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill.Postgres
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import zio.Scope
import zio.ZIO
import zio.ZLayer
import zio.test.TestAspect
import zio.test.assertTrue

object OrderQuerierSpec extends DatabaseSpec:

  // TODO: I would implement my own integration with Scala-Testcontainers, but for the sake of the project I used this one which is little slow.

  val beginning = LocalDateTime
    .now()
    .withYear(2024)
    .truncatedTo(ChronoUnit.SECONDS)

  val ending = beginning.plusMonths(13)

  val layer = Scope.default >>> postgresLayerWithMigrate >>> ZLayer {
    for
      quill        <- ZIO.service[Postgres[SnakeCase]]
      orderQuerier <- OrderQuerier(quill)
    yield orderQuerier
  }

  override def spec = suite("An OrderQuerier should")(
    test("return no order.") {
      for
        querier <- ZIO.service[OrderQuerier]
        result  <- querier.byInterval(QueryInterval(beginning, ending)).runCollect
      yield assertTrue(
        result.isEmpty
      )
    }.provide(layer) @@ TestAspect.withLiveClock,
    test("return all orders") {
      for
        querier <- ZIO.service[OrderQuerier]
        count   <- querier.byInterval(QueryInterval(beginning.withYear(2019), ending.withYear(2022))).runCount
      yield assertTrue(
        count == 4000
      )
    }.provide(layer) @@ TestAspect.withLiveClock,
    test("return some orders") {
      val beginning = LocalDateTime.of(2021, 2, 1, 0, 0, 0)
      val ending    = LocalDateTime.of(2021, 6, 30, 0, 0, 0)
      for
        querier <- ZIO.service[OrderQuerier]
        count   <-
          querier
            .byInterval(QueryInterval(beginning, ending))
            .runCount
      yield assertTrue(
        count == 1492
      )
    }.provide(layer) @@ TestAspect.withLiveClock
  ) @@ TestAspect.sequential
