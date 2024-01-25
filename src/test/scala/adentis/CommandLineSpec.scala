package adentis

import java.time.LocalDateTime
import zio.test.assertTrue

object CommandLineSpec extends AdentisSpec:

  override def spec = suite("The CommandLine object")(
    suite("should accept")(
      test("report and interval") {
        for commandLine <-
            CommandLine("--report" :: "1-5 5-13 >13" :: "2011-01-01 01:01:01" :: "2021-01-01 23:59:00" :: Nil)
        yield assertTrue(
          commandLine.query == QueryInterval(
            LocalDateTime.of(2011, 1, 1, 1, 1, 1),
            LocalDateTime.of(2021, 1, 1, 23, 59, 0)
          ),
          commandLine.report == Seq(
            ReportInterval.Between(1, 5),
            ReportInterval.Between(5, 13),
            ReportInterval.GreaterThan(13)
          )
        )
      },
      test("only interval") {
        for commandLine <-
            CommandLine("2011-01-01 01:01:01" :: "2021-01-01 23:59:00" :: Nil)
        yield assertTrue(
          commandLine.query == QueryInterval(
            LocalDateTime.of(2011, 1, 1, 1, 1, 1),
            LocalDateTime.of(2021, 1, 1, 23, 59, 0)
          ),
          commandLine.report == Seq(
            ReportInterval.Between(1, 3),
            ReportInterval.Between(4, 6),
            ReportInterval.Between(7, 12),
            ReportInterval.GreaterThan(12)
          )
        )
      }
    )
  )
