package user

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import com.typesafe.config.{Config, ConfigFactory}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import user.secvice.UserEntity
import user.routes.{ClientActor, ClientRoutes}
import user.service.PostgresClient

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Promise}
import scala.io.StdIn
import scala.sys.ShutdownHookThread
import scala.util.Try

object Boot extends App with ClientRoutes {

  val config: Config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("UserShardSystem", config)

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val cluster = Cluster.get(system)
  override def actorSys: ActorSystem = system

  val databaseConfig                 = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")
  implicit val session: SlickSession = SlickSession.forConfig(databaseConfig)

  val client = PostgresClient()

  val region = ClusterSharding(system).start(
    typeName = UserEntity.shardName,
    entityProps = UserEntity.props(client),
    settings = ClusterShardingSettings(system),
    extractEntityId = UserEntity.idExtractor,
    extractShardId = UserEntity.shardResolver)

  override val someProps: Props = ClientActor.props(region)

  cluster.registerOnMemberUp {
    //    val serverSource = Http().bind(interface = "localhost", port = 8080)

    val bindingFuture = Http().bindAndHandle(route, "127.0.0.1", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

    //    HttpServer(region, signerRegion, authRequestUrl, amqpProducer, emailCheck).startServer()
    //    log.debug(s"Customer API server running at 0.0.0.0:8080")
    //    Await.result(system.whenTerminated, Duration.Inf)
  }

  setShutDown(config, cluster)

  system.registerOnTermination(session.close())
  system.registerOnTermination(println("Shutting down Actor System."))

  def setShutDown(config: Config, cluster: Cluster)(implicit system: ActorSystem): ShutdownHookThread = {
    val shutDownHookTimeout: Duration = Try {
      FiniteDuration(config.getDuration("shutdown-hook-timeout").toNanos, TimeUnit.NANOSECONDS)
    }.getOrElse(FiniteDuration(50, TimeUnit.SECONDS))

    sys.addShutdownHook {

      try {

        //        log.debug("SHUTDOWN HOOK: Leaving cluster.")

        println("SHUTDOWN HOOK: Closing connection.")
        session.close()
        val shutdownPromise = Promise[Unit]()

        cluster.registerOnMemberRemoved {
          shutdownPromise.complete(_)
        }

        cluster.leave(cluster.selfMember.uniqueAddress.address)

        Await.ready(shutdownPromise.future, shutDownHookTimeout)

      } catch {
        case e: Throwable =>
        //          log.error("Error while leaving cluster: ex=" + e.getMessage)
      }
    }
  }
//
//  Http().bindAndHandle(route, "0.0.0.0", 8001)
//  println(s"Server running at http://localhost:8080/")
//  Await.result(system.whenTerminated, Duration.Inf)
//
//  system.registerOnTermination(session.close())
//  system.registerOnTermination(println("Shutting down Actor System."))
//
//  sys.addShutdownHook {
//
//    try {
//      println("SHUTDOWN HOOK: Closing connection.")
//      session.close()
//    } catch {
//      case e: Throwable =>
//        println("Error while leaving cluster: ex=" + e.getMessage)
//    }
//  }

}
