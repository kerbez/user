package user.routes

import akka.actor.{Actor, ActorRef, Props}
import user.commands.UserCommand.{CreateAdminCommand, CreateClientCommand, DeleteClientCommand, UpdateClientCommand}
import user.{CreateAdmin, CreateClient, DeleteClient, UpdateClient}

object ClientActor {
  def props(prop: ActorRef) = Props(new ClientActor(prop))
}

class ClientActor(region: ActorRef) extends Actor{
  override def receive: Receive = {
    case cmd: CreateClient =>
      println(s"Got CreateClient: $cmd")
//      context.actorOf(prop) ! CreateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      region ! CreateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: UpdateClient =>
      println(s"Got CreateClient: $cmd")
//      context.actorOf(prop) ! UpdateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      region ! UpdateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: DeleteClient =>
      println(s"Got CreateClient: $cmd")
//      context.actorOf(prop) ! DeleteClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      region ! DeleteClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd: CreateAdmin =>
      println(s"Got CreateClient: $cmd")
//      context.actorOf(prop) ! CreateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      region ! CreateAdminCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
      context.become(waitingResponse(sender()))

    case cmd => println(s"Got any: $cmd")
  }

  def waitingResponse(replyTo: ActorRef): Receive = {
    case a =>
      replyTo ! a
  }
}