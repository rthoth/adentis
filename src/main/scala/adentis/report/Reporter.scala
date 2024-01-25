package adentis.report

import adentis.LocalDateService
import adentis.ReportInterval
import adentis.model.Order
import zio.Console
import zio.RIO
import zio.Task
import zio.ZIO
import zio.stream.ZStream

import java.time.LocalDateTime

object Reporter:

  private case class Count(interval: ReportInterval, value: Long):

    def increment(): Count = copy(value = value + 1)

  def report(orders: ZStream[Any, Throwable, Order], template: Seq[ReportInterval]): RIO[LocalDateService, Unit] =
    for
      initial <- ZIO.succeed(for interval <- template yield Count(interval, 0))
      now     <- LocalDateService.now()
      result  <- orders.runFold(initial)(count(now))
      _       <- ZIO.foreach(result)(print)
    yield ()

  private def count(now: LocalDateTime)(state: Seq[Count], order: Order): Seq[Count] =
    for count <- state
    yield {
      order.items.foldLeft(count) { (count, item) =>
        if count.interval.test(now, item.product.createdAt) then count.increment()
        else count
      }
    }

  private def print(count: Count): Task[Unit] =
    ZIO.attempt {
      count.interval match
        case ReportInterval.Between(beginning, ending) => s"$beginning-$ending months"
        case ReportInterval.GreaterThan(value)         => s">$value months"
        case _                                         => throw IllegalStateException("Unexpected interval!")
    } flatMap (interval => Console.printLine(s"$interval: ${count.value}"))
