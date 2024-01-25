package adentis.database.schema

import io.getquill.*
import java.time.LocalDateTime

case class OrderTable(
    id: String,
    createdAt: LocalDateTime
)

val orderTable = quote {
  querySchema[OrderTable]("orders")
}

case class ItemTable(
    id: String,
    orderId: String,
    productId: String,
    price: Double,
    quantity: Int
)

val itemTable = quote {
  querySchema[ItemTable]("items")
}

case class ProductTable(
    id: String,
    name: String,
    createdAt: LocalDateTime
)

val productTable = quote {
  querySchema[ProductTable]("products")
}
