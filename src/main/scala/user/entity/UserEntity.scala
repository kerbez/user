package user.entity

import akka.actor.{Actor, Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion
import akka.event.{Logging, LoggingAdapter}
import akka.stream.Materializer
import org.json4s._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtJson4s}
import org.json4s.JsonDSL._
import user.{Accepted, ClientInfo, Error, MailAgent, PostgresClient, RestWithHeader, TokenResponse, User}
import user.commands.UserCommand
import user.commands.UserCommand._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object UserEntity {

  def props(client: PostgresClient)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) = Props(new UserEntity(client: PostgresClient)(executionContext, materializer))

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: UserCommand => (cmd.userId, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: UserCommand =>
      (math.abs(cmd.userId.hashCode) % 100).toString
  }

  val shardName: String = "UserShard"
}

class UserEntity(client: PostgresClient)(implicit executionContext: ExecutionContextExecutor, materializer: Materializer) extends Actor {
  import UserEntity._
  val log: LoggingAdapter = Logging(context.system, this)

 context.setReceiveTimeout(600.seconds)
  def receive: Receive = {
    case cmd: VerifyEmailCommand =>
      log.info(s"[init] Got VerifyEmailCommand: $cmd")
      val replyTo = sender()

      client.find(cmd.userId).onComplete {
        case Success(usr) =>
          val u = collectUser(usr)
          u match {
            case Some(user) =>
              log.info(s"Got Success find user: $user")
              val code = 1000 + Random.nextInt( (9999 - 1000) + 1 )
              MailAgent.start(cmd.email, "kerbez2898@gmail.com", "Habit Tracker application email verification code", code.toString, "smtp.gmail.com").sendMessage()
              context.become(waitingCode(cmd.email, code.toString))
              replyTo ! Accepted("201",
                "Code sent")
            case None =>
              log.info(s"User not found: $None")
              replyTo ! Error("120",
                "User not found")
              stopEntity()
          }
        case Failure(exception) =>
          log.info(s"Got Failure find exception: $exception")
          replyTo ! TokenResponse(120, s"Got error when request db $exception")
          stopEntity()
      }
    case cmd: GetClientCommand =>
      log.info(s"[init] Got GetClientCommand: $cmd")
      val replyTo = sender()

      client.find(cmd.userId).onComplete {
        case Success(found) =>
          found match {
            case Some(x) =>
              log.debug(s"Got Success find value: $x")
              val info = ClientInfo(x._1, x._2, x._4, x._8)
              replyTo ! info
              stopEntity()
            case None =>
              log.debug(s"Got Success to find value: $found")
              replyTo ! Error("110", "User does't exist")
              stopEntity()
          }
        case Failure(exception) =>
          log.debug(s"Got Failure find exception: $exception")
          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
          stopEntity()
      }
    case cmd: VerificationCodeCommand =>
      log.info(s"Got unhandled VerificationCodeCommand: $cmd")
      sender() ! Error("134", "Code expired")
      stopEntity()

    case cmd: CreateClientCommand =>
      log.info(s"[init] Got CreateClientCommand: $cmd")
      val replyTo = sender()

      val user = User(cmd.userId, cmd.nikName, cmd.password, None, 0)

      client.insert(user.userId, user.nikName, user.password, user.rating).onComplete {
        case Success(cnt) =>
          log.info(s"Got Success insert cnt: $cnt")
          replyTo ! TokenResponse(201,
            "User successfully created!",
            Some(tokenGenerate(user.nikName, user.password)))
          stopEntity()
        case Failure(exception) =>
          log.info(s"Got Failure insert exception: $exception")
          replyTo ! TokenResponse(404, "User can not be created! " + exception.toString)
          stopEntity()
      }




//      client.find(cmd.userId).onComplete {
//        case Success(value) =>
//          value match {
//            case Some(x) =>
//              log.info(s"Got Success find value: $value")
//              replyTo ! TokenResponse(201, "User already exists")
//            case None =>
//              log.info(s"Got Success to find value: $value")
//
//          }
//        case Failure(exception) =>
//          log.info(s"Got Failure find exception: $exception")
//          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//      }

//    case cmd: UpdateClientCommand =>
//      log.info(s"[init] Got UpdateClientCommand: $cmd")
//      val replyTo = sender()
//
//      val user = User(cmd.userId, cmd.password, cmd.mobile, cmd.rating)
//
//      client.find(cmd.mobile).onComplete {
//        case Success(found) =>
//          found match {
//            case Some(x) =>
//              log.info(s"Got Success find value: $found")
//              if(checkClientToken(x._1, x._3, cmd.token)){
//                client.updateClient(cmd.userId, user.userName, user.password, user.rating).onComplete {
//                  case Success(value) =>
//                    if(value) {
//                      log.info(s"Got Success update value: $value")
//                      replyTo ! Accepted("200", "OK")
//                    } else {
//                      log.info(s"Got Success update value: $value")
//                      replyTo ! Error("120", "Failed to request db")
//                    }
//                  case Failure(exception) =>
//                    replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//                }
//              }
//              else{
//                replyTo ! Error("403", "Access Denied")
//              }
//            case None =>
//              log.info(s"Got Success to find value: $found")
//              replyTo ! Error("110", "User does't exist")
//          }
//        case Failure(exception) =>
//          log.info(s"Got Failure find exception: $exception")
//          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//      }

//    case cmd: DeleteClientCommand =>
//      log.info(s"[init] Got DeleteClientCommand: $cmd")
//      val replyTo = sender()
//
//      client.find(cmd.userId).onComplete {
//        case Success(found) =>
//          found match {
//            case Some(x) =>
//              log.debug(s"Got Success find value: $found")
//              if(checkClientToken(x._1, x._3, cmd.token)) {
//                client.delete(cmd.userId).onComplete {
//                  case Success(value) =>
//                    if (value) {
//                      log.debug(s"Got Success delete value: $found")
//                      replyTo ! Accepted("200", "OK")
//                    } else {
//                      log.debug(s"Got Success delete value: $found")
//                      replyTo ! Error("120", "Failed to request db")
//                    }
//                  case Failure(exception) =>
//                    log.debug(s"Failed to request db delete, exception: " + exception.toString)
//                    replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//                }
//              }
//              else{
//                log.debug(s"Got access denied for user: ${x._1}")
//                replyTo ! Error("403", "Access Denied")
//              }
//            case None =>
//              log.debug(s"Got Success to find value: $found")
//              replyTo ! Error("110", "User does't exist")
//          }
//        case Failure(exception) =>
//          log.debug(s"Got Failure find exception: $exception")
//          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//      }

//    case cmd: GetClientTokenCommand =>
//      log.info(s"[init] Got GetClientCommand: $cmd")
//      val replyTo = sender()
//
//      client.find(cmd.userId).onComplete {
//        case Success(found) =>
//          found match {
//            case Some(x) =>
//              log.debug(s"Got Success find value: $x")
//              if(cmd.userId == x._1 && cmd.password == x._3) {
//                replyTo ! TokenResponse(201,
//                  "Token successfully generated!",
//                  Some(tokenGenerate(cmd.userId, cmd.password)))
//              }
//              else {
//                replyTo ! Error("110", "Wrong password")
//              }
//            case None =>
//              log.debug(s"Got Success to find value: $found")
//              replyTo ! Error("110", "User does't exist")
//          }
//        case Failure(exception) =>
//          log.debug(s"Got Failure find exception: $exception")
//          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//      }
//    case cmd: CheckClientTokenCommand =>
//      log.info(s"[init] Got GetClientCommand: $cmd")
//      val replyTo = sender()
//
//      client.find(cmd.userId).onComplete {
//        case Success(found) =>
//          found match {
//            case Some(x) =>
//              log.debug(s"Got Success find value: $x")
//              if(checkClientToken(x._1, x._3, cmd.token)) {
//                replyTo ! Accepted("200", "Success")
//              }
//              else {
//                replyTo ! Error("110", "Wrong token")
//              }
//            case None =>
//              log.debug(s"Got Success to find value: $found")
//              replyTo ! Error("110", "User does't exist")
//          }
//        case Failure(exception) =>
//          log.debug(s"Got Failure find exception: $exception")
//          replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)
//      }


    case ReceiveTimeout =>
      log.info("Got ReceiveTimeout while waiting code")
      //      context.become(receive)
      stopEntity()

    case cmd: DeleteAdminCommand =>
    case cmd: GetAdminCommand =>
    case any =>
      log.info(s"Got any: $any")
      println(s"Got any $any")

  }


  def waitingCode(email: String, code: String): Receive = {
    case cmd: VerificationCodeCommand =>
      val replyTo = sender()
      if(code == cmd.code) {
        client.updateVerifiedEmail(cmd.userId, email, true).onComplete {
          case Success(value) =>
            if(value) {
              log.info(s"Got Success update value: $value")
              replyTo ! Accepted("201", "email verified successfully")
              context.become(receive)
            } else {
              log.info(s"Got Success update value: $value")
              replyTo ! Error("120", "Failed to update email")
              //you can try to resend same code
            }
          case Failure(exception) =>
            log.info("Failed to request db" + exception.toString)
            replyTo ! Error("120", "Failed to request db" + exception.toString)
            //you can try to resend same code
        }
      } else {
        replyTo ! Accepted("133", "wrong code")
        context.become(receive)
        stopEntity()
      }
    case cmd: VerifyEmailCommand =>
      log.info(s"[waitingCode] Got VerifyEmailCommand: $cmd")
      val replyTo = sender()

      client.find(cmd.userId).onComplete {
        case Success(usr) =>
          val u = collectUser(usr)
          u match {
            case Some(user) =>
              log.info(s"Got Success find user: $user")
              val code = 1000 + Random.nextInt( (9999 - 1000) + 1 )
              MailAgent.start(cmd.email, "kerbez2898@gmail.com", "Habit Tracker application email verification code", code.toString, "smtp.gmail.com").sendMessage()
              context.become(waitingCode(cmd.email, code.toString))
              replyTo ! Accepted("201",
                "Code sent")
            case None =>
          }
        case Failure(exception) =>
          log.info(s"Got Failure find exception: $exception")
          replyTo ! TokenResponse(120, s"Got error when request db $exception")
      }
    case ReceiveTimeout =>
      log.info("Got ReceiveTimeout while waiting code")
//      context.become(receive)
      stopEntity()
  }

  def stopEntity(): Unit = {
    context.stop(self)
  }

  def collectUser(tuple: Option[(String, String, String, String, Boolean, Boolean, String, Int, String)]): Option[User] = {
    tuple match {
      case Some(x) =>
        Some(User(x._1, x._2, x._3, Some(x._4), x._8))
      case None =>
        None
    }
  }

  def checkClientToken(mobile: String, password: String, token: String): Boolean = {
    println(tokenGenerate(mobile, password) + " != " + token)
    (tokenGenerate(mobile, password) == token)
  }

  private def tokenGenerate(nikName: String, password: String): String = {
    val claim     = JObject(("nikName", nikName), ("password", password))
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
