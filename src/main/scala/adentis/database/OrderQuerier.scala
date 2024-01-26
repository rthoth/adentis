package adentis.database

import adentis.QueryInterval
import adentis.database.schema.*
import adentis.model.Item
import adentis.model.Order
import adentis.model.Product
import io.getquill.*
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import io.github.arainko.ducktape.*
import zio.RIO
import zio.Task
import zio.ZIO
import zio.stream.ZStream
import io.getquill.extras.LocalDateTimeOps

import java.time.LocalDateTime

trait OrderQuerier {

  def byInterval(query: QueryInterval): ZStream[Any, Throwable, Order]
}

object OrderQuerier {

  def apply(quill: Quill.Postgres[SnakeCase]): Task[OrderQuerier] = ZIO.attempt {
    Impl(quill)
  }

  def fromEnvironment: RIO[Quill.Postgres[SnakeCase], OrderQuerier] =
    ZIO.serviceWithZIO(this.apply)

  private case class Holder(order: OrderTable, items: Seq[(ItemTable, ProductTable)]):

    def build(): Order =
      order
        .into[Order]
        .transform(
          Field.const(_.items, buildItems())
        )

    def buildItems(): Seq[Item] =
      for (item, product) <- items yield {
        item
          .into[Item]
          .transform(
            Field.const(_.product, buildProduct(product))
          )
      }

    def buildProduct(product: ProductTable): Product =
      product.into[Product].transform()

    def hold(item: ItemTable, product: ProductTable): Holder = copy(
      items = items :+ (item -> product)
    )

  private type Tuple = (OrderTable, ItemTable, ProductTable)

  private class Impl(quill: Quill.Postgres[SnakeCase]) extends OrderQuerier:

    import quill.*

    override def byInterval(interval: QueryInterval): ZStream[Any, Throwable, Order] =
      stream {
        for
          o <- orderTable
                 .filter(o => o.createdAt >= lift(interval.beginning) && o.createdAt < lift(interval.ending))
                 .sortBy(_.id)
          i <- itemTable.join(_.orderId == o.id)
          p <- productTable.join(_.id == i.productId)
        yield Some((o, i, p))
      }.concat(ZStream.succeed(None))
        .mapAccum(None)(accumulate)
        .collect { case Some(order) => order }

    private def accumulate(
        holder: Option[Holder],
        tuple: Option[Tuple]
    ): (Option[Holder], Option[Order]) =
      tuple match
        case Some((order, item, product)) =>
          holder match
            case Some(holder) if holder.order.id == order.id =>
              (Some(holder.hold(item, product)), None)
            case Some(holder)                                =>
              (Some(Holder(order, Seq(item -> product))), Some(holder.build()))
            case None                                        =>
              (Some(Holder(order, Seq(item -> product))), None)
        case None                         =>
          holder match
            case Some(holder) => (None, Some(holder.build()))
            case None         => (None, None)

}
