package user.secvice

import akka.actor.{Actor, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.Materializer
import org.json4s._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtJson4s}
import user.commands.UserCommand.CreateClientCommand
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import user.service.ElasticFunctionality

import scala.concurrent.ExecutionContextExecutor

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


  def props(elasticFuncs: ElasticFunctionality)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) = Props(new UserEntity(elasticFuncs)(executionContext,materializer))
}

class UserEntity(elasticFuncs: ElasticFunctionality)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) extends Actor {
  import UserEntity._
  val log: LoggingAdapter = Logging(context.system, this)

  //  context.setReceiveTimeout(Duration(150, SECONDS))

  def receive: Receive = {
    case cmd: CreateClientCommand =>
      log.info(s"[init] Got CreateClientCommand: $cmd")
      val replyTo = sender()

      val user = User(cmd.userName, cmd.password, cmd.mobile, 0)

      elasticFuncs
        .ifUserExists(user.mobile)
        .map { res =>
          if (res) replyTo ! TokenResponse(201, "User already exists")
          else {
            //            val hashedUser = user.copy(publicName = Hasher(user.privateName + DateTime.now.toString).sha256.hash)
            elasticFuncs
              .createUser(user)
              .map(
                _ =>
                  replyTo ! TokenResponse(201,
                    "User successfully created!",
                    Some(tokenGenerate(user.mobile, user.password)))
              )
              .recover {
                case _: Exception => replyTo ! TokenResponse(404, "User can not be created!")
              }
          }
        }
        .recover {
          case _: Exception => replyTo ! TokenResponse(404, "User can not be created!")
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
