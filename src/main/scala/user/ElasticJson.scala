package user

import com.sksamuel.elastic4s.{Hit, HitReader, Indexable}
import user.secvice.UserEntity.User
import org.json4s.native.Serialization.write
import org.json4s.native.JsonMethods.parse

import scala.util.Try


trait ElasticJson extends Json4sSerializer {

  implicit object ReceivedUserIndexable extends Indexable[User] {
    override def json(t: User): String = write(t)
  }

  implicit object UserHitReader extends HitReader[User] {
    override def read(hit: Hit): Try[User] = Try(parse(hit.sourceAsString).extract[User])
  }

}
