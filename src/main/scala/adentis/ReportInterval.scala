package adentis

trait ReportInterval

object ReportInterval:

  val default: Seq[ReportInterval] = Seq(
    Between(1, 3),
    Between(4, 6),
    Between(7, 12),
    GreaterThan(12)
  )

  final case class Between(beginning: Int, ending: Int) extends ReportInterval

  final case class GreaterThan(reference: Int) extends ReportInterval
