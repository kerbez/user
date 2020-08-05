package user

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.http.scaladsl.Http
import akka.stream.alpakka.slick.scaladsl.SlickSession
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import user.entity.RegistrationActor
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import user.entity.UserEntity
import user.routes.{ClientActor, ClientRoutes}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContextExecutor, Promise}
import scala.sys.ShutdownHookThread
import scala.util.Try

object Boot extends App with ClientRoutes{
//  val log: Logger = LoggerFactory.getLogger("Boot")

  val config = ConfigFactory.load()
//  val clusterName = "UserShardSystem"
//  val host = "127.0.0.1"
//  val port = 8100
//val appConfig =  com.typesafe.config.ConfigFactory.parseFile(new File("src/main/resources/application.conf")).resolve()

//  val config = ConfigFactory.load().withFallback(appConfig)
//  val clusterName = "UserShardSystem"
//  val host = "127.0.0.1"
//  val port = 2551
  println(s"config: $config")
  val clusterName = config.getString("clustering.cluster.name")
  val host = config.getString("clustering.ip")
  val port = config.getInt("clustering.port")
  implicit val system: ActorSystem = ActorSystem(clusterName, config)

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


  val cluster = Cluster.get(system)

//  AkkaManagement(system).start()
//
//  ClusterBootstrap(system).start()

//  log.debug(s"> Cluster name: $clusterName")

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


  val registrationProps = RegistrationActor.props(region, client)
  override val someProps: Props = ClientActor.props(region, registrationProps, client)

//  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
//      println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
//      StdIn.readLine() // let it run until user presses return
//      bindingFuture
//        .flatMap(_.unbind()) // trigger unbinding from the port
//        .onComplete(_ => system.terminate()) // and shutdown when done

  cluster.registerOnMemberUp {
//    val serverSource = Http().bind(interface = host, port = port)

    Http().bindAndHandle(route, "0.0.0.0", 8101)
    println(s"Server online at http://$host:8101/\nPress RETURN to stop...")
//    StdIn.readLine() // let it run until user presses return
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done

//    HttpServer(region, signerRegion, authRequestUrl, amqpProducer, emailCheck).startServer()
//    log.debug(s"Customer API server running at 0.0.0.0:8080")
    Await.result(system.whenTerminated, Duration.Inf)
  }

  setShutDown(config, cluster)

  def setShutDown(config: Config, cluster: Cluster)(implicit system: ActorSystem): ShutdownHookThread = {
    system.registerOnTermination(session.close())
    system.registerOnTermination(println("Shutting down Actor System."))
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
  def getConfig: Config = {
    val a = """akka {
              |  actor {
              |    provider = "akka.cluster.ClusterActorRefProvider"
              |  }
              |
              |  remote {
              |    log-remote-lifecycle-events = off
              |    enabled-transports = ["akka.remote.netty.tcp"]
              |    netty.tcp {
              |#      hostname = "127.0.0.1"
              |#      port = 2551
              |      hostname = ${clustering.ip}
              |      port = ${clustering.port}
              |;       post = 2551
              |            # external (logical) port
              |#       bind-port = 2551   # internal (bind) port
              |    }
              |  }
              |
              |  cluster {
              |    roles = ["core"]
              |    role {
              |      core.min-nr-of-members = 1
              |    }
              |    sharding = {
              |      role = "core"
              |    }
              |#    seed-nodes = ["akka.tcp://UserShardSystem@127.0.0.1:2551]
              |    seed-nodes = ["akka.tcp://"${clustering.cluster.name}"@"${clustering.seed-ip}":"${clustering.seed-port}]
              |    auto-down-unreachable-after = off
              |
              |    metrics.enabled = off
              |    failure-detector.threshold = 10.0
              |    failure-detector.acceptable-heartbeat-pause = 5s
              |    downing-provider-class = com.ajjpj.simpleakkadowning.SimpleAkkaDowningProvider
              |  }
              |}
              |
              |slick-postgres {
              |  profile = "slick.jdbc.PostgresProfile$"
              |  db {
              |    dataSourceClass = "slick.jdbc.DriverDataSource"
              |    properties = {
              |      driver = "org.postgresql.Driver"
              |      url = "jdbc:postgresql://46.254.20.220:5431/habbit"
              |      user = "admin"
              |      password = "postgres"
              |    }
              |    minimumIdle = 10
              |    maximumPoolSize = 20
              |  }
              |}
              |
              |akka-downing {
              |  active-strategy = keep-majority
              |  stable-after = 10s
              |  down-removal-margin = 20s
              |  keep-majority {
              |    role = "core"
              |  }
              |}
              |
              |clustering {
              | ip = "172.28.1.5"
              | ip = ${?CLUSTER_IP}
              | ip = "127.0.0.1"
              |#  port = 8080
              | port = 2551
              |#  port = ${?CLUSTER_PORT}
              | port = 8100
              | seed-ip = "172.28.1.5"
              | seed-ip = ${?CLUSTER_IP}
              | seed-ip = "127.0.0.1"
              | seed-ip = ${?SEED_PORT_1600_TCP_ADDR}
              | seed-port = ${?SEED_PORT_1600_TCP_PORT}
              |# seed-port = 2551
              | seed-port = 8100
              | cluster.name = "UserShardSystem"
              |}
              |""".stripMargin
    ConfigFactory.parseString(a)
  }
}
