package user.secvice

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.Materializer
import org.json4s._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtJson4s}
import user.commands.UserCommand.CreateClientCommand
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import user.service.PostgresClient

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object UserEntity {

  final case class User(userName: String,
                        password: String,
                        mobile: String,
                        rating: Int)

  final case class TokenResponse(
                                  statusCode: Int,
                                  description: String,
                                  jwtToken: Option[String] = None
                                )


  def props(client: PostgresClient)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) = Props(new UserEntity(client)(executionContext,materializer))
}

class UserEntity(client: PostgresClient)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) extends Actor {
  import UserEntity._
  val log: LoggingAdapter = Logging(context.system, this)

  //  context.setReceiveTimeout(Duration(150, SECONDS))

  def receive: Receive = {
    case cmd: CreateClientCommand =>
      log.info(s"[init] Got CreateClientCommand: $cmd")
      val replyTo = sender()

      val user = User(cmd.userName, cmd.password, cmd.mobile, 0)

      client.find(cmd.mobile).onComplete {
        case Success(value) =>
          value match {
            case Some(x) =>
              log.info(s"Got Success find value: $value")
              replyTo ! TokenResponse(201, "User already exists")
            case None =>
              log.info(s"Got Success to find value: $value")
              client.insert(user.mobile, user.userName, user.password, user.rating).onComplete {
                case Success(cnt) =>
                  log.info(s"Got Success insert cnt: $cnt")
                  replyTo ! TokenResponse(201,
                    "User successfully created!",
                    Some(tokenGenerate(user.mobile, user.password)))
                case Failure(exception) =>
                  log.info(s"Got Failure insert exception: $exception")
                  replyTo ! TokenResponse(404, "User can not be created! " + exception.toString)
              }
          }
        case Failure(exception) =>
          log.info(s"Got Failure find exception: $exception")
          replyTo ! TokenResponse(404, "Failed to request db")

      }



    case any =>
      log.info(s"Got any: $any")
      println(s"Got any $any")

  }



  private def tokenGenerate(mobile: String, password: String): String = {
    val claim     = JObject(("mobile", mobile), ("password", password))
    val key       = "secretKey"
    val algorithm = JwtAlgorithm.HS256
    JwtJson4s.encode(claim, key, algorithm)
  }

  def checkToken(headers: Map[String, String]): Boolean = {
    val bearerToken = headers.getOrElse("Authorization", "")
    val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
    val key         = "secretKey"
    val algorithm   = JwtAlgorithm.HS256
    Jwt.decode(token, key, Seq(algorithm)).isSuccess
  }
}
