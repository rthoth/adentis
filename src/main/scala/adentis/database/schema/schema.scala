package adentis.database.schema

import io.getquill.*
import java.time.LocalDateTime

case class StoredOrder(
    id: String,
    createdAt: LocalDateTime
)

val orderTable = quote {
  querySchema[StoredOrder]("orders")
}

case class StoredItem(
    id: String,
    orderId: String,
    productId: String,
    price: Double,
    quantity: Int
)

val itemTable = quote {
  querySchema[StoredItem]("items")
}

case class StoredProduct(
    id: String,
    name: String,
    createdAt: LocalDateTime
)

val productTable = quote {
  querySchema[StoredProduct]("products")
}
