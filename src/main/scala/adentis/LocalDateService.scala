package adentis

import java.time.LocalDateTime
import zio.ULayer
import zio.URIO
import zio.ZIO
import zio.ZLayer

trait LocalDateService:

  def now(): LocalDateTime

object LocalDateService:

  val live: ULayer[LocalDateService] = ZLayer.succeed(Live())

  def fixed(localDateTime: LocalDateTime): ULayer[LocalDateService] = ZLayer.succeed(Fixed(localDateTime))
    
  def now(): URIO[LocalDateService, LocalDateTime] =
    ZIO.serviceWith(_.now())

  private class Live extends LocalDateService:

    override def now(): LocalDateTime = LocalDateTime.now()

  private class Fixed(localDateTime: LocalDateTime) extends LocalDateService:

    override def now(): LocalDateTime = localDateTime
