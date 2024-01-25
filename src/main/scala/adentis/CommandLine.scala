package adentis

import java.time.LocalDateTime
import scala.annotation.tailrec
import zio.Task
import zio.ZIO

object CommandLine:

  def apply(args: Seq[String]): Task[CommandLine] = ZIO.attempt {
    val iterator = args.iterator

    val LocalDateTimeRegex = """^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})$""".r

    def readQueryInterval(beginning: String, query: Option[QueryInterval]): QueryInterval =
      if query.isEmpty then
        iterator.nextOption() match
          case Some(LocalDateTimeRegex(ending)) =>
            QueryInterval(
              LocalDateTime.parse(beginning.replace(' ', 'T')),
              LocalDateTime.parse(ending.replace(' ', 'T'))
            )

          case Some(value) =>
            throw IllegalArgumentException(s"It was expected a date-time after $beginning, but it was $value.")

          case None =>
            throw IllegalArgumentException(s"It was expected a date-time after $beginning, but it ended.")
      else throw IllegalArgumentException(s"An interval ${query.get} was already defined!")

    def readReport(previous: Option[Seq[ReportInterval]]): Seq[ReportInterval] =
      if previous.isEmpty then
        val BetweenRegex = """^(\d+)-(\d+)$""".r
        val GreaterRegex = """^>(\d+)$""".r

        iterator.nextOption() match
          case Some(value) =>
            val reports = for piece <- value.split("""\s+""") yield piece match
              case BetweenRegex(beginning, ending) => ReportInterval.Between(beginning.toInt, ending.toInt)
              case GreaterRegex(reference)         => ReportInterval.GreaterThan(reference.toInt)
              case _                               => throw IllegalArgumentException(s"Invalid report parameter: $piece")

            if reports.nonEmpty then reports
            else throw IllegalArgumentException("You should provide a list of report period!")

          case None => throw IllegalArgumentException("It must be defined a report range!")
      else throw IllegalArgumentException("Argument report can be defined only once!")

    @tailrec
    def readNext(query: Option[QueryInterval], report: Option[Seq[ReportInterval]]): CommandLine =
      iterator.nextOption() match
        case Some(LocalDateTimeRegex(value)) =>
          readNext(Some(readQueryInterval(value, query)), report)

        case Some("--report") =>
          readNext(query, Some(readReport(report)))

        case Some(unexpected) =>
          throw IllegalArgumentException(unexpected)

        case None =>
          query match
            case Some(value) => CommandLine(value, report.getOrElse(ReportInterval.default))
            case None        => throw IllegalArgumentException("A query interval must be defined!")

    readNext(None, None)
  }

case class CommandLine(query: QueryInterval, report: Seq[ReportInterval])
