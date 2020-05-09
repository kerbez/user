package user.routes

import akka.actor.{Actor, ActorRef, Props}
import akka.event.{Logging, LoggingAdapter}
import pdi.jwt.{Jwt, JwtAlgorithm}
import user.commands.UserCommand._
import user._

object ClientActor {
  def props(prop: ActorRef) = Props(new ClientActor(prop))
}

class ClientActor(region: ActorRef) extends Actor{

  val log: LoggingAdapter = Logging(context.system, this)

  override def receive: Receive = {
    case restWithHeader: RestWithHeader =>
      log.debug(s"Got restWithHeader: $restWithHeader")
//      region ! restWithHeader
      restWithHeader.restMessage match {
        case cmd: CreateClient =>
          log.debug(s"Got restWithHeader CreateClient: $cmd")
          if(checkToken(restWithHeader.header)) {
            region ! CreateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password)
            context.become(waitingResponse(sender()))
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }
        case cmd: UpdateClient =>
          log.debug(s"Got restWithHeader UpdateClient: $cmd")
          val bearerToken = restWithHeader.header.getOrElse("Authorization", "")
          val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
          region ! UpdateClientCommand(cmd.id, cmd.userName, cmd.mobile, cmd.password, cmd.rating, token)
          context.become(waitingResponse(sender()))
        case cmd: DeleteClient =>
          log.debug(s"Got DeleteClient: $cmd")
          val bearerToken = restWithHeader.header.getOrElse("Authorization", "")
          val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
          region ! DeleteClientCommand(cmd.id, token)
          context.become(waitingResponse(sender()))

        case cmd: GetClient =>
          log.debug(s"Got GetClient: $cmd")
          if(checkToken(restWithHeader.header)) {
            region ! GetClientCommand(cmd.id)
            context.become(waitingResponse(sender()))
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: GetClientToken =>
          log.debug(s"Got GetClientToken: $cmd")
          if(checkToken(restWithHeader.header)) {
            region ! GetClientTokenCommand(cmd.id, cmd.password)
            context.become(waitingResponse(sender()))
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }

        case cmd: CheckClientToken =>
          log.debug(s"Got CheckClientToken: $cmd")
          if(checkToken(restWithHeader.header)) {
            region ! CheckClientTokenCommand(cmd.id, cmd.token)
            context.become(waitingResponse(sender()))
          }
          else {
            log.debug(s"Got checkToken: false")
            sender() ! Error("403", "Access denied")
          }
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
      replyTo ! a
  }
  def checkToken(headers: Map[String, String]): Boolean = {
    val bearerToken = headers.getOrElse("Authorization", "")
    val token       = if (bearerToken.nonEmpty) bearerToken.split(" ")(1) else ""
    val key         = "secretKey"
    val algorithm   = JwtAlgorithm.HS256
    Jwt.decode(token, key, Seq(algorithm)).isSuccess
  }
}