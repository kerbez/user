package user

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.typesafe.config.{Config, ConfigFactory}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import user.secvice.UserEntity
import user.routes.{ClientActor, ClientRoutes}
import user.service.PostgresClient

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Promise}
import scala.io.StdIn

object Boot extends App with ClientRoutes {

  val config: Config = ConfigFactory.load()
  implicit val system: ActorSystem = ActorSystem("UserShardSystem")

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  override def actorSys: ActorSystem = system

  val databaseConfig                 = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")
  implicit val session: SlickSession = SlickSession.forConfig(databaseConfig)

  val client = PostgresClient()

  override val someProps: Props = ClientActor.props(UserEntity.props(client))


  Http().bindAndHandle(route, "0.0.0.0", 8001)
  println(s"Server running at http://localhost:8080/")
  Await.result(system.whenTerminated, Duration.Inf)

  system.registerOnTermination(session.close())
  system.registerOnTermination(println("Shutting down Actor System."))

  sys.addShutdownHook {

    try {
      println("SHUTDOWN HOOK: Closing connection.")
      session.close()
    } catch {
      case e: Throwable =>
        println("Error while leaving cluster: ex=" + e.getMessage)
    }
  }

}
