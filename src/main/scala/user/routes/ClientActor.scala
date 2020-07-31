package user.routes

import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import org.json4s.JObject
import org.json4s.JsonDSL._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtJson4s}
import user.commands.UserCommand._
import user.{GeneralResponse, _}
import user.commands.RegistrationCommands.{CheckEmailCommand, CheckNikNameCommand, RegisterCommand}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object ClientActor {
  def props(region: ActorRef, registrationActor: Props, client: PostgresClient)(implicit executionContext: ExecutionContextExecutor): Props = Props(new ClientActor(region, registrationActor, client)(executionContext))
}

class ClientActor(region: ActorRef, registrationActor: Props, client: PostgresClient)(implicit executionContext: ExecutionContextExecutor) extends Actor{

  val log: LoggingAdapter = Logging(context.system, this)

  override def receive: Receive = {
    case restWithHeader: RestWithHeader =>
      log.debug(s"Got restWithHeader: $restWithHeader")
//      region ! restWithHeader
      restWithHeader.restMessage match {
//        case cmd: CreateClient =>
//          log.debug(s"Got restWithHeader CreateClient: $cmd")
//          if(checkToken(restWithHeader.header)) {
//            region ! CreateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
//            context.become(waitingResponse(sender()))
//          }
//          else {
//            log.debug(s"Got checkToken: false")
//            sender() ! Error("403", "Access denied")
//          }
//        case cmd: UpdateClient =>
//          log.debug(s"Got restWithHeader UpdateClient: $cmd")
//          val bearerToken = restWithHeader.header.getOrElse("Authorization", "")
//          val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
//          region ! UpdateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password, cmd.rating, token)
//          context.become(waitingResponse(sender()))
//        case cmd: DeleteClient =>
//          log.debug(s"Got DeleteClient: $cmd")
//          val bearerToken = restWithHeader.header.getOrElse("Authorization", "")
//          val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
//          region ! DeleteClientCommand(cmd.id, token)
//          context.become(waitingResponse(sender()))

        case cmd: GetUserById =>
          val replyTo = sender()
          log.debug(s"Got GetUserById: $cmd")
          if(checkToken(restWithHeader.header)) {
            region ! GetClientCommand(cmd.id)
            context.become(waitingResponse(sender()))
          }
          else {
            log.debug(s"Got checkToken: false")
            replyTo ! Error("403", "Access denied")
          }

        case cmd: GetUserByEmail =>
          val replyTo = sender()
          log.debug(s"Got GetUserByEmail: $cmd")
          if(checkToken(restWithHeader.header)) {
            client.findEmail(cmd.email).onComplete {
              case Success(res) =>
                res match {
                  case Some(usr) =>
                    region ! GetClientCommand(usr._1)
                    context.become(waitingResponse(replyTo))
                  case None =>
                    replyTo ! Error("120", "Wrong Email")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            replyTo ! Error("403", "Access denied")
          }

        case cmd: GetUserByNikName =>
          val replyTo = sender()
          log.debug(s"Got GetUserByNikName: $cmd")
          if(checkToken(restWithHeader.header)) {
            client.findNikName(cmd.nikName).onComplete {
              case Success(res) =>
                res match {
                  case Some(usr) =>
                    region ! GetClientCommand(usr._1)
                    context.become(waitingResponse(replyTo))
                  case None =>
                    replyTo ! Error("120", "Wrong Email")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            replyTo ! Error("403", "Access denied")
          }

        case cmd: GetUserTokenByNikName =>
          log.info(s"Got GetUserTokenByNikName: $cmd")
          val replyTo = sender()
          if(checkToken(restWithHeader.header)) {
            client.findNikName(cmd.nikName).onComplete{
              case Success(value) =>
                value match {
                  case Some(usr) =>
                    if(usr._3 == cmd.password){
                      replyTo ! TokenResponse(201, "Got token", Some(tokenGenerate(cmd.nikName, cmd.password)))
                    } else {
                      log.info(s"${usr._3} != ${cmd.password}")
                      replyTo ! Error("120", "Wrong Password")
                    }
                  case None =>
                    replyTo ! Error("120", "Wrong NikName")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            replyTo ! Error("403", "Access denied")
          }

        case cmd: GetUserTokenByEmail =>
          log.info(s"Got GetUserTokenByEmail: $cmd")
          val replyTo = sender()
          if(checkToken(restWithHeader.header)) {
            client.findEmail(cmd.email).onComplete{
              case Success(value) =>
                value match {
                  case Some(usr) =>
                    if(usr._3 == cmd.password){
                      replyTo ! TokenResponse(201, "Got token", Some(tokenGenerate(usr._2, cmd.password)))
                    } else {
                      log.info(s"${usr._3} != ${cmd.password}")
                      replyTo ! Error("120", "Wrong Password")
                    }
                  case None =>
                    replyTo ! Error("120", "Wrong Email")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            replyTo ! Error("403", "Access denied")
          }


        case cmd: CheckUserTokenByNikName =>
          log.debug(s"Got CheckUserTokenByNikName: $cmd")
          val replyTo = sender()
          if(checkToken(restWithHeader.header)) {
            client.findNikName(cmd.nikName).onComplete{
              case Success(value) =>
                value match {
                  case Some(usr) =>
                    if(checkClientToken(cmd.nikName, usr._3, cmd.token)){
                      replyTo ! Accepted("201", "Success")
                    } else {
                      replyTo ! Error("120", "Wrong Token")
                    }
                  case None =>
                    replyTo ! Error("120", "Wrong NikName")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: CheckUserTokenByEmail =>
          log.debug(s"Got CheckUserTokenByEmail: $cmd")
          val replyTo = sender()
          if(checkToken(restWithHeader.header)) {
            client.findEmail(cmd.email).onComplete{
              case Success(value) =>
                value match {
                  case Some(usr) =>
                    if(checkClientToken(usr._2, usr._3, cmd.token)){
                      replyTo ! Accepted("201", "Success")
                    } else {
                      replyTo ! Error("120", "Wrong Token")
                    }
                  case None =>
                    replyTo ! Error("120", "Wrong Email")
                }
              case Failure(exception) =>
                replyTo ! Error("120", s"$exception")
            }
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: Register =>
          log.debug(s"Got SendEmail: $cmd")
          if(checkToken(restWithHeader.header)) {
            context.actorOf(registrationActor) ! RegisterCommand(cmd.email, cmd.password)
            context.become(waitingResponse(sender()))
          } else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: CheckEmail =>
          log.debug(s"Got CheckEmail: $cmd")
          if(checkToken(restWithHeader.header)) {
            context.actorOf(registrationActor) ! CheckEmailCommand(cmd.email)
            context.become(waitingResponse(sender()))
          } else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: CheckNikName =>
          log.info(s"Got CheckNikName: $cmd")
          if(checkToken(restWithHeader.header)) {
            context.actorOf(registrationActor) ! CheckNikNameCommand(cmd.nikName)
            context.become(waitingResponse(sender()))
          } else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: VerifyEmail =>
          log.info(s"Got VerifyEmail: $cmd")
          val replyTo = sender()
          val userId = getIdByToken(cmd.nikName, restWithHeader.header)
          userId.onComplete{
            case Success(usr) =>
              log.info(s"got Id by token: $usr")
              region ! VerifyEmailCommand(usr, cmd.email)
              context.become(waitingResponse(replyTo))
            case Failure(exception) =>
              replyTo ! Error("120", exception.getMessage)
          }

        case cmd: VerificationCode =>
          log.debug(s"Got VerificationCode: $cmd")
          val replyTo = sender()
          region ! VerificationCodeCommand(cmd.id, cmd.code)
          context.become(waitingResponse(replyTo))

        case cmd: ResendEmailCode =>
          log.debug(s"Got ResendEmailCode: $cmd")
          val replyTo = sender()
          if(checkToken(restWithHeader.header)) {
            region ! ResendEmailCodeCommand(cmd.id)
            context.become(waitingResponse(replyTo))
          } else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: UpdateNikName =>
          log.debug(s"Got UpdateNikName: $cmd")
          val replyTo = sender()
          checkNikName(cmd.nikName).onComplete {
            case Success(value) =>
              if(!value) {
                log.info("nikName is available")
                if(checkToken(restWithHeader.header)) {
                  client.find(cmd.id).onComplete{
                    case Success(value) =>
                      value match {
                        case Some(usr) =>
                          if(checkClientToken(usr._2, usr._3, cmd.token)){
                            log.info(s"UserToken is correct")
                            region ! UpdateNikNameCodeCommand(cmd.id, cmd.nikName)
                            context.become(waitingResponse(replyTo))
                          } else {
                            log.info(s"UserToken is incorrect")
                            replyTo ! Error("120", "Wrong Token")
                          }
                        case None =>
                          log.info(s"User not found")
                          replyTo ! Error("120", "User not found")
                      }
                    case Failure(exception) =>
                      log.info(s"Got exception: $exception")
                      replyTo ! Error("120", s"$exception")
                  }
                }
                else {
                  log.debug(s"Got checkToken: false")
                  replyTo ! Error("403", "Access denied")
                }
              }
              else {
                log.info(s"nikName is unavailable: $value")
                replyTo ! GeneralResponse("131", "nikName is unavailable")
              }
            case Failure(exception) =>
              log.error("Failed to request db, exception: " + exception.toString)
              replyTo ! Error("120", "Failed to request db, exception: " + exception.toString)

          }



//          if(checkToken(restWithHeader.header)) {
//            region ! SendEmailCommand(cmd.id, cmd.token, cmd.email, cmd.subject, cmd.content)
//            context.become(waitingResponse(sender()))
//          }
//          else {
//            log.debug(s"Got checkToken: false")
//            sender() ! Error("403", "Access denied")
//          }

      }

//    case cmd: CreateAdmin =>
//      log.debug(s"Got CreateAdmin: $cmd")
//      region ! CreateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
//      context.become(waitingResponse(sender()))
//
//    case cmd: UpdateAdmin =>
//      log.debug(s"Got UpdateAdmin: $cmd")
//      region ! UpdateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
//      context.become(waitingResponse(sender()))
//
//    case cmd: DeleteAdmin =>
//      log.debug(s"Got DeleteAdmin: $cmd")
//      region ! DeleteAdminCommand(cmd.id)
//      context.become(waitingResponse(sender()))
//
//    case cmd: GetAdmin =>
//      log.debug(s"Got GetAdmin: $cmd")
//      region ! GetAdminCommand(cmd.id)
//      context.become(waitingResponse(sender()))

    case cmd => println(s"Got any: $cmd")
  }

  def waitingResponse(replyTo: ActorRef): Receive = {
    case a =>
      log.info(s"ClientActor Got response: $a")
      replyTo ! a
  }


  def getIdByToken(nikName: String, headers: Map[String, String]): Future[String] = {
    val bearerToken = headers.getOrElse("Authorization", "")
    val token       = if (bearerToken.split(" ").length > 1) bearerToken.split(" ")(1) else ""
    client.findNikName(nikName).flatMap {
      case Some(usr) =>
        if (checkClientToken(nikName, usr._3, token)) {
          Future.successful(usr._1)
        } else Future.failed(
          new IllegalStateException(s"Wrong token"))
      case None =>
        Future.failed(
          new IllegalStateException(s"NikName not found"))
    }
  }


  def checkClientToken(nikName: String, password: String, token: String): Boolean = {
    println(tokenGenerate(nikName, password) + " != " + token)
    (tokenGenerate(nikName, password) == token)
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

  def checkNikName(nikName: String): Future[Boolean] = {
    for {
      res1 <- client.findNikName(nikName)
    } yield {
      log.info(s"res1: ${res1}")
      res1.isDefined
    }
  }

  def checkEmail(email: String): Future[Boolean] = {
    for {
      res1 <- client.findEmail(email)
    } yield {
      log.info(s"res1: ${res1}")
      res1.isDefined
    }
  }
}