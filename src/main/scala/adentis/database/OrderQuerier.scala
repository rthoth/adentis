package adentis.database

import adentis.QueryInterval
import adentis.database.schema.*
import adentis.model.Item
import adentis.model.Order
import adentis.model.Product
import io.getquill.*
import io.getquill.SnakeCase
import io.getquill.extras.given
import io.getquill.given
import io.getquill.jdbczio.Quill
import io.github.arainko.ducktape.*
import java.time.LocalDateTime
import scala.util.Random
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

  private class Impl(quill: Quill.Postgres[SnakeCase]) extends OrderQuerier:

    import quill.*

    override def byInterval(interval: QueryInterval): ZStream[Any, Throwable, Order] =
      ZStream
        .fromZIO(
          run {
            orderTable.filter(o => o.createdAt >= lift(interval.beginning) && o.createdAt < lift(interval.ending))
          }
        )
        .flattenIterables
        .mapZIO(order =>
          for items <- queryItems(order.id)
          yield order
            .into[Order]
            .transform(
              Field.const(_.items, items)
            )
        )

    private def queryItems(orderId: String): Task[Seq[Item]] =
      for queryResult <- run {
                           for
                             i <- itemTable if i.orderId == lift(orderId)
                             p <- productTable.join(_.id == i.productId)
                           yield (i, p)
                         }
      yield {
        for (item, product) <- queryResult
        yield

          val convertedProduct = product.into[Product].transform()

          item
            .into[Item]
            .transform(
              Field.const(_.product, convertedProduct)
            )
      }

}
