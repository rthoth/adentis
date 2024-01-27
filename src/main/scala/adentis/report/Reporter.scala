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

  private case class Counter(interval: ReportInterval, value: Long):

    def accept(now: LocalDateTime, reference: LocalDateTime): Boolean =
      interval.accept(now, reference)

    def increment(): Counter = copy(value = value + 1)

  def report(orders: ZStream[Any, Throwable, Order], template: Seq[ReportInterval]): RIO[LocalDateService, Unit] =
    for
      initialState <- ZIO.succeed(for interval <- template yield Counter(interval, 0))
      now          <- LocalDateService.now()
      result       <- orders.runFold(initialState)(computeNewState(now))
      _            <- ZIO.foreach(result)(print)
    yield ()

  private def computeNewState(now: LocalDateTime)(state: Seq[Counter], order: Order): Seq[Counter] =
    for counter <- state
    yield count(now, counter, order.items)

  @tailrec
  private def count(now: LocalDateTime, counter: Counter, items: Seq[Item]): Counter =
    items.headOption match
      case Some(item) =>
        if counter.accept(now, item.createdAt) then counter.increment()
        else count(now, counter, items.tail)

      case _ =>
        counter

  private def print(counter: Counter): Task[Unit] =
    ZIO.attempt {
      counter.interval match
        case ReportInterval.Between(beginning, ending) => s"$beginning-$ending months"
        case ReportInterval.GreaterThan(value)         => s">$value months"
        case _                                         => throw IllegalStateException("Unexpected interval!")
    } flatMap (text => Console.printLine(s"$text: ${counter.value}"))
