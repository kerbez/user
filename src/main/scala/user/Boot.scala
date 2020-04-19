package user

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.http.scaladsl.Http
//import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
//import akka.management.cluster.bootstrap.ClusterBootstrap
//import akka.management.scaladsl.AkkaManagement
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import user.entity.UserEntity
import user.routes.{ClientActor, ClientRoutes}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Promise}
import scala.io.StdIn
import scala.sys.ShutdownHookThread
import scala.util.Try
//import org.slf4j.{Logger, LoggerFactory}

object Boot extends App with ClientRoutes{
//  val log: Logger = LoggerFactory.getLogger("Boot")

//  val config = ConfigFactory.load()
//  val clusterName = config.getString("akka.management.cluster.bootstrap.contact-point-discovery.service-name")
  implicit val system: ActorSystem = ActorSystem()

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


//  val cluster = Cluster.get(system)

//  AkkaManagement(system).start()
//
//  ClusterBootstrap(system).start()

//  log.debug(s"> Cluster name: $clusterName")

  override def actorSys: ActorSystem = implicitly

//  val region = ClusterSharding(system).start(
//    typeName = UserEntity.shardName,
//    entityProps = Props[UserEntity],
//    settings = ClusterShardingSettings(system),
//    extractEntityId = UserEntity.idExtractor,
//    extractShardId = UserEntity.shardResolver)


  override val someProps: Props = ClientActor.props(UserEntity.props)

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
      println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
      StdIn.readLine() // let it run until user presses return
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done

//  cluster.registerOnMemberUp {
//    val serverSource = Http().bind(interface = "localhost", port = 8080)
//
//    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
//    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
//    StdIn.readLine() // let it run until user presses return
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done
//
////    HttpServer(region, signerRegion, authRequestUrl, amqpProducer, emailCheck).startServer()
////    log.debug(s"Customer API server running at 0.0.0.0:8080")
////    Await.result(system.whenTerminated, Duration.Inf)
//  }
//
//  setShutDown(config, cluster)
//
//  def setShutDown(config: Config, cluster: Cluster)(implicit system: ActorSystem): ShutdownHookThread = {
//    val shutDownHookTimeout: Duration = Try {
//      FiniteDuration(config.getDuration("shutdown-hook-timeout").toNanos, TimeUnit.NANOSECONDS)
//    }.getOrElse(FiniteDuration(50, TimeUnit.SECONDS))
//
//    sys.addShutdownHook {
//
//      try {
//
////        log.debug("SHUTDOWN HOOK: Leaving cluster.")
//
//        val shutdownPromise = Promise[Unit]()
//
//        cluster.registerOnMemberRemoved {
//          shutdownPromise.complete(_)
//        }
//
//        cluster.leave(cluster.selfMember.uniqueAddress.address)
//
//        Await.ready(shutdownPromise.future, shutDownHookTimeout)
//
//      } catch {
//        case e: Throwable =>
////          log.error("Error while leaving cluster: ex=" + e.getMessage)
//      }
//    }
//  }
}
