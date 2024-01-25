package adentis

object AdentisException:
  case class Database(message: String, cause: Throwable = null) extends AdentisException(message, cause)

abstract class AdentisException(message: String, cause: Throwable) extends RuntimeException(message, cause)
