package user

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{Formats, ShortTypeHints, jackson}
import org.json4s.jackson.Serialization
import user.secvice.UserEntity.User

trait Json4sSerializer extends Json4sSupport {
  implicit val serialization: Serialization.type = jackson.Serialization

  implicit val formats: Formats =
    Serialization.formats(
      ShortTypeHints(List(classOf[User]))
    )
}
