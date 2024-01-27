package adentis

import adentis.model.Item
import scala.util.Random

object ItemFixture:

  def createRandom(): Item = Item(
    product = ProductFixture.createRandom(),
    price = Random.nextDouble() * 100,
    quantity = Random.nextInt(5)
  )
