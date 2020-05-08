package user.routes

import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import user.commands.UserCommand._
import user._

object ClientActor {
  def props(prop: ActorRef) = Props(new ClientActor(prop))
}

class ClientActor(region: ActorRef) extends Actor{

  val log: LoggingAdapter = Logging(context.system, this)

  override def receive: Receive = {
    case cmd: CreateClient =>
      log.debug(s"Got CreateClient: $cmd")
      region ! CreateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: UpdateClient =>
      log.debug(s"Got UpdateClient: $cmd")
      region ! UpdateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password, cmd.rating)
      context.become(waitingResponse(sender()))

    case cmd: DeleteClient =>
      log.debug(s"Got DeleteClient: $cmd")
      region ! DeleteClientCommand(cmd.id)
      context.become(waitingResponse(sender()))

    case cmd: GetClient =>
      log.debug(s"Got GetClient: $cmd")
      region ! GetClientCommand(cmd.id)
      context.become(waitingResponse(sender()))

    case cmd: CreateAdmin =>
      log.debug(s"Got CreateAdmin: $cmd")
      region ! CreateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: UpdateAdmin =>
      log.debug(s"Got UpdateAdmin: $cmd")
      region ! UpdateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: DeleteAdmin =>
      log.debug(s"Got DeleteAdmin: $cmd")
      region ! DeleteAdminCommand(cmd.id)
      context.become(waitingResponse(sender()))

    case cmd: GetAdmin =>
      log.debug(s"Got GetAdmin: $cmd")
      region ! GetAdminCommand(cmd.id)
      context.become(waitingResponse(sender()))

    case cmd => println(s"Got any: $cmd")
  }

  def waitingResponse(replyTo: ActorRef): Receive = {
    case a =>
      replyTo ! a
  }
}