package adentis

import adentis.model.Order
import java.util.UUID

object  OrderFixture:

  def createRandom(): Order = Order(
    id = Fixture.createRandomId(),
    createdAt = Fixture.createRandomLocalDateTime(),
    items = for _ <- 0 until 10 yield ItemFixture.createRandom()
  )
