package user

import com.sksamuel.elastic4s.ElasticDsl._
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.sksamuel.elastic4s.ElasticDsl.{createIndex, indexExists}
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.typesafe.config.{Config, ConfigFactory}
import user.secvice.UserEntity
import user.routes.{ClientActor, ClientRoutes}
import user.service.ElasticFunctionality

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Promise}
import scala.io.StdIn
import scala.sys.ShutdownHookThread
import scala.util.Try

object Boot extends App with ClientRoutes {

  val config: Config = ConfigFactory.load()
  //  val clusterName = config.getString("akka.management.cluster.bootstrap.contact-point-discovery.service-name")
  implicit val system: ActorSystem = ActorSystem("UserShardSystem", config)

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  //  val log = Logging(system, this)
  //  val log = Logging(system, this)

  //  log.info("hi")

  override def actorSys: ActorSystem = system


  val usersIndex: String     = config.getString("elastic.indexes.users")
  val elasticHosts: String   = config.getString("elastic.hosts")
  val elasticPorts: String   = config.getString("elastic.ports")
  val externalUri: String    = config.getString("uri")
  val elasticClient: ElasticClient = ElasticClient(
    JavaClient(ElasticProperties(s"http://$elasticHosts:$elasticPorts"))
  )
  val elasticFuncs = new ElasticFunctionality(elasticClient, usersIndex)

  override val someProps: Props = ClientActor.props(UserEntity.props(elasticFuncs))

  if (!elasticClient.execute(indexExists(usersIndex)).await.result.isExists)
    elasticClient.execute(createIndex(usersIndex))
  else system.log.info(s"$usersIndex already exists")

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done





  //  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  //      println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  //      StdIn.readLine() // let it run until user presses return
  //      bindingFuture
  //        .flatMap(_.unbind()) // trigger unbinding from the port
  //        .onComplete(_ => system.terminate()) // and shutdown when done

  //  cluster.registerOnMemberUp {
  //    val serverSource = Http().bind(interface = "localhost", port = 8080)

  //    HttpServer(region, signerRegion, authRequestUrl, amqpProducer, emailCheck).startServer()
  //    log.debug(s"Customer API server running at 0.0.0.0:8080")
  //    Await.result(system.whenTerminated, Duration.Inf)
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
