package adentis.model

import java.time.LocalDateTime

case class Order(
    id: String,
    createdAt: LocalDateTime,
    items: Seq[Item]
)
