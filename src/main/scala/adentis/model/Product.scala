package adentis.model

import java.time.LocalDateTime

case class Product(
    id: String,
    name: String,
    createdAt: LocalDateTime
)
