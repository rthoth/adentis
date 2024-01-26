package adentis.model

import java.time.LocalDateTime

case class Item(
    product: Product,
    price: Double,
    quantity: Int
):

  def createdAt: LocalDateTime = product.createdAt
