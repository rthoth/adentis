package adentis

import java.time.LocalDateTime
import zio.UIO
import zio.ULayer
import zio.URIO
import zio.ZIO
import zio.ZLayer

trait LocalDateService:

  def now(): LocalDateTime

object LocalDateService:

  val live: ULayer[LocalDateService] = ZLayer.succeed(Impl())

  def now(): URIO[LocalDateService, LocalDateTime] =
    ZIO.serviceWith(_.now())

  private class Impl extends LocalDateService:
    
    override def now(): LocalDateTime = LocalDateTime.now()
