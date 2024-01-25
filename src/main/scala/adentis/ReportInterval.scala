package adentis

import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.ChronoUnit

trait ReportInterval:

  def test(now: LocalDateTime, reference: LocalDateTime): Boolean

object ReportInterval:

  val default: Seq[ReportInterval] = Seq(
    Between(1, 3),
    Between(4, 6),
    Between(7, 12),
    GreaterThan(12)
  )

  final case class Between(beginning: Int, ending: Int) extends ReportInterval:

    override def test(now: LocalDateTime, reference: LocalDateTime): Boolean =
      val value = reference.until(now, ChronoUnit.MONTHS)
      value >= beginning && value < ending

  final case class GreaterThan(value: Int) extends ReportInterval:

    override def test(now: LocalDateTime, reference: LocalDateTime): Boolean =
      reference.until(now, ChronoUnit.MONTHS) > value
