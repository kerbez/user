package user

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.persistence.cassandra.ConfigSessionProvider
import com.typesafe.config.Config

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class SessionProvider(system: ActorSystem, config: Config) extends ConfigSessionProvider(system, config) {

  override def lookupContactPoints(
                                    clusterId: String
                                  )(implicit ec: ExecutionContext): Future[immutable.Seq[InetSocketAddress]] = {
    val contactPoints: immutable.Seq[String] = config.getString("contact-points").split(',').to[immutable.Seq]
    Future.successful(buildContactPoints(contactPoints, port))
  }
}
