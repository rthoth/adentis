package adentis

import adentis.report.Reporter
import zio.ZIO
import zio.stream.ZStream
import zio.test.TestConsole
import zio.test.assertTrue

object ReporterSpec extends AdentisSpec:

  override def spec = suite("The Reporter should")(
    test("make a report correctly") {
      val now = Fixture.createRandomLocalDateTime()

      val stream = ZStream
        .fromIterable {
          var createdAt = now.minusMonths(1)
          val order     = OrderFixture.createRandom()
          Seq(order.copy(items = for _ <- 0 until 13 yield {
            val item    = ItemFixture.createRandom()
            val newItem = item.copy(product =
              item.product.copy(
                createdAt = createdAt
              )
            )
            createdAt = createdAt.minusMonths(1)
            newItem
          }))
        }

      for
        _      <- Reporter
                    .report(stream, ReportInterval.default)
                    .provide(LocalDateService.fixed(now))
        output <- TestConsole.output
      yield assertTrue(
        output == Vector(
          "1-3 months: 1\n",
          "4-6 months: 1\n",
          "7-12 months: 1\n",
          ">12 months: 1\n"
        )
      )
    }
  )
