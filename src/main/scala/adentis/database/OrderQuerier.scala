package adentis.database

import adentis.QueryInterval
import adentis.database.schema.*
import adentis.model.Item
import adentis.model.Order
import adentis.model.Product
import io.getquill.*
import io.getquill.SnakeCase
import io.getquill.extras.LocalDateTimeOps
import io.getquill.jdbczio.Quill
import io.github.arainko.ducktape.*
import java.time.LocalDateTime
import zio.RIO
import zio.Task
import zio.ZIO
import zio.stream.ZStream

trait OrderQuerier {

  def byInterval(query: QueryInterval): ZStream[Any, Throwable, Order]
}

object OrderQuerier {

  def apply(quill: Quill.Postgres[SnakeCase]): Task[OrderQuerier] = ZIO.attempt {
    Impl(quill)
  }

  def fromEnvironment: RIO[Quill.Postgres[SnakeCase], OrderQuerier] =
    ZIO.serviceWithZIO(this.apply)

  private case class Holder(order: StoredOrder, items: Seq[(StoredItem, StoredProduct)], count: Long):

    def build(): Order =
      order
        .into[Order]
        .transform(
          Field.const(_.items, buildItems())
        )

    private def buildItems(): Seq[Item] =
      for (item, product) <- items yield {
        item
          .into[Item]
          .transform(
            Field.const(_.product, buildProduct(product))
          )
      }

    private def buildProduct(product: StoredProduct): Product =
      product.into[Product].transform()

    def hold(item: StoredItem, product: StoredProduct): Holder = copy(
      items = items :+ (item -> product)
    )

  private type Tuple = (StoredOrder, StoredItem, StoredProduct)

  private class Impl(quill: Quill.Postgres[SnakeCase]) extends OrderQuerier:

    import quill.*

    override def byInterval(interval: QueryInterval): ZStream[Any, Throwable, Order] =

      val queryOrder = quote {
        for
          o <- orderTable
                 .filter(o => o.createdAt >= lift(interval.beginning) && o.createdAt <= lift(interval.ending))
          i <- itemTable.join(_.orderId == o.id)
          p <- productTable.join(_.id == i.productId)
        yield (o, i, p)
      }

      stream {
        for tuple <- queryOrder.sortBy(_._1.id) yield Some(tuple)
      }.concat(ZStream.succeed(None))
        .mapAccum(None)(accumulate)
        .collect { case Some(order) => order }

    private def accumulate(
        state: Option[Holder],
        element: Option[Tuple]
    ): (Option[Holder], Option[Order]) =
      element match
        case Some((order, item, product)) =>
          state match
            case Some(holder) if holder.order.id == order.id =>
              (Some(holder.hold(item, product)), None)
            case Some(holder)                                =>
              (Some(Holder(order, Seq(item -> product), holder.count + 1L)), Some(holder.build()))
            case None                                        =>
              (Some(Holder(order, Seq(item -> product), 1L)), None)
        case None                         =>
          state match
            case Some(holder) => (None, Some(holder.build()))
            case None         => (None, None)

}
