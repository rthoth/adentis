package adentis

import adentis.model.Product

object ProductFixture:

  def createRandom(): Product = Product(
    id = Fixture.createRandomId(),
    name = s"product-${Fixture.createRandomId()}",
    createdAt = Fixture.createRandomLocalDateTime()
  )
