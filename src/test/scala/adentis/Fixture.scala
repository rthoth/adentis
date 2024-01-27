package adentis

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import scala.util.Random

object Fixture:

  def createRandomLocalDateTime(): LocalDateTime =
    LocalDateTime
      .now()
      .truncatedTo(ChronoUnit.SECONDS)
      .plusSeconds(Random.nextLong(60L))
      .plusDays(Random.nextLong(20) - 10L)
      .plusMonths(Random.nextLong(24) - 12L)
      .plusYears(Random.nextLong(4) - 2)

  def createRandomId(): String =
    java.lang.Long.toString(Random.nextLong().abs, 32)
