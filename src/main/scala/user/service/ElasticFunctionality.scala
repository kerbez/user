package user.service

import akka.event.{Logging, LoggingAdapter}
import com.roundeights.hasher.Hasher
import com.sksamuel.elastic4s.{ElasticClient, Response}
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.requests.delete.DeleteResponse
import com.sksamuel.elastic4s.requests.indexes.IndexResponse
import com.sksamuel.elastic4s.requests.update.UpdateResponse
import com.sksamuel.exts.Logging
import user.ElasticJson
import user.secvice.UserEntity.User

import scala.concurrent.{ExecutionContext, Future}

class ElasticFunctionality(
                            elasticClient: ElasticClient,
                            usersIndex: String
                          )(
                            implicit executionContext: ExecutionContext
                          ) extends ElasticJson with Logging{


  def ifUserExists(id: String): Future[Boolean] = {
    elasticClient.execute {
      exists(id, usersIndex)
    }.map(_.result)
  }

  def createUser(user: User): Future[Response[IndexResponse]] = {

    val userWithHashedPass = user.copy(password = Hasher(user.password).sha256)

    elasticClient.execute {
      indexInto(usersIndex).doc(userWithHashedPass).id(user.mobile)
    }
  }
}