package user.entity

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import user.{Error, GeneralResponse, PostgresClient}
import user.commands.RegistrationCommands.{CheckEmailCommand, CheckNikNameCommand, RegisterCommand}
import user.commands.UserCommand.CreateClientCommand
import java.util.UUID.randomUUID

import akka.event.{Logging, LoggingAdapter}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object RegistrationActor {
  def props(region: ActorRef, client: PostgresClient)(implicit executionContext: ExecutionContextExecutor): Props = Props(new RegistrationActor(region, client)(executionContext))
}

class RegistrationActor(region: ActorRef, client: PostgresClient)(implicit executionContext: ExecutionContextExecutor) extends Actor{

  val log: LoggingAdapter = Logging(context.system, this)

  context.setReceiveTimeout(60.seconds)
  override def receive: Receive = {
    case cmd: RegisterCommand =>
      val replyTo = sender()
      check(cmd.nikName).onComplete {
        case Success(res) =>
          res match {
            case Some(user) =>
              replyTo ! GeneralResponse("131", "nikName is unavailable")
              context.stop(self)
//            case (_, Some(user)) =>
//              replyTo ! GeneralResponse("132", "email is unavailable")
//              context.stop(self)
            case None =>
              val userId = randomUUID().toString
              region ! CreateClientCommand(userId, cmd.nikName, cmd.password)
              context.become(waitingEntity(replyTo))
          }
        case Failure(ex) =>
          replyTo ! Error("120", "Failed to request db, exception: " + ex.toString)
          context.stop(self)
      }

    case cmd: CheckEmailCommand =>
      val replyTo = sender()
      client.findEmail(cmd.email).onComplete {
        case Success(res) =>
          res match {
            case Some(user) =>
              replyTo ! GeneralResponse("132", "email is unavailable")
              context.stop(self)
            case None =>
              replyTo ! GeneralResponse("200", "email is available")
              context.stop(self)
          }
        case Failure(ex) =>
          replyTo ! Error("120", "Failed to request db, exception: " + ex.toString)
          context.stop(self)
      }

    case cmd: CheckNikNameCommand =>
      log.info(s"RegistrationActor got cmd: $cmd")
      val replyTo = sender()
      client.findNikName(cmd.nikName).onComplete {
        case Success(res) =>
          res match {
            case Some(user) =>
              log.info(s"RegistrationActor findNikName got user: $user")
              replyTo ! GeneralResponse("131", "nikName is unavailable")
              context.stop(self)
            case None =>
              log.info(s"RegistrationActor findNikName got user: $None")
              replyTo ! GeneralResponse("200", "nikName is available")
              context.stop(self)
          }
        case Failure(ex) =>
          log.info(s"RegistrationActor findNikName got exception: $ex")
          replyTo ! Error("120", "Failed to request db, exception: " + ex.toString)
          context.stop(self)
      }

    case ReceiveTimeout =>
      log.info("Got ReceiveTimeout while waiting code")
      //      context.become(receive)
      context.stop(self)
  }

  def waitingEntity(replyTo: ActorRef): Receive = {
    case a =>
      replyTo ! a
      context.stop(self)
  }


  def check(nikName: String): Future[Option[(String, String, String, String, Boolean, Boolean, String, Int, String)]] = {
    for {
      res1 <- client.findNikName(nikName)
    } yield {
      res1
    }
  }

}
