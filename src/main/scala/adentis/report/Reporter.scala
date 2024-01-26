package adentis.report

import adentis.LocalDateService
import adentis.ReportInterval
import adentis.model.Item
import adentis.model.Order
import java.time.LocalDateTime
import scala.annotation.tailrec
import zio.Console
import zio.RIO
import zio.Task
import zio.ZIO
import zio.stream.ZStream

object Reporter:

  private case class IntervalCount(interval: ReportInterval, value: Long):

    def accept(now: LocalDateTime, reference: LocalDateTime): Boolean =
      interval.accept(now, reference)

    def increment(): IntervalCount = copy(value = value + 1)

  def report(orders: ZStream[Any, Throwable, Order], template: Seq[ReportInterval]): RIO[LocalDateService, Unit] =
    for
      initial <- ZIO.succeed(for interval <- template yield IntervalCount(interval, 0))
      now     <- LocalDateService.now()
      result  <- orders.runFold(initial)(count(now))
      _       <- ZIO.foreach(result)(print)
    yield ()

  private def count(now: LocalDateTime)(state: Seq[IntervalCount], order: Order): Seq[IntervalCount] =
    for intervalCount <- state
    yield {
      countInterval(now, intervalCount, order.items)
    }

  @tailrec
  private def countInterval(now: LocalDateTime, intervalCount: IntervalCount, items: Seq[Item]): IntervalCount =
    items.headOption match
      case Some(item) if intervalCount.accept(now, item.createdAt) =>
        intervalCount.increment()
      case Some(_)                                                                =>
        countInterval(now, intervalCount, items.tail)
      case None                                                                   =>
        intervalCount

  private def print(count: IntervalCount): Task[Unit] =
    ZIO.attempt {
      count.interval match
        case ReportInterval.Between(beginning, ending) => s"$beginning-$ending months"
        case ReportInterval.GreaterThan(value)         => s">$value months"
        case _                                         => throw IllegalStateException("Unexpected interval!")
    } flatMap (interval => Console.printLine(s"$interval: ${count.value}"))
