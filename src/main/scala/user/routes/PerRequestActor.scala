package user.routes

import java.util.UUID

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, ReceiveTimeout, SupervisorStrategy}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{RequestContext, RouteResult}
import user.{Accepted, ClientInfo, Error, JsonSupport, RestMessage, RestWithHeader, TokenResponse}
import spray.json._
import user.routes.PerRequestActor.WithProps

import scala.concurrent.Promise
import scala.concurrent.duration._

trait PerRequestActor extends Actor with JsonSupport {
  import PerRequestActor._
  import context._

  def r: RequestContext
  def target: ActorRef
  def message: RestWithHeader
  def p: Promise[RouteResult]

  setReceiveTimeout(2.seconds)
  target ! message

  override def receive: Receive = {
    case str: String => complete(OK, str)
    case tokenResponse: TokenResponse => complete(OK, tokenResponse)
    case clientInfo: ClientInfo => complete(OK, clientInfo)
    case accepted: Accepted => complete(OK, accepted)
    case e: Error => complete(InternalServerError, e)
    case ReceiveTimeout => complete(GatewayTimeout, RequestTimeoutResponse)
  }

  def complete(m: => ToResponseMarshallable): Unit = {
    val f = r.complete(m)
    f.onComplete(p.complete(_))
    stop(self)
  }

  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy() {
      case e =>
        complete(InternalServerError, Error("100", e.getMessage))
        Stop
    }
}

object PerRequestActor {
  val EmptyJson = "{}".parseJson
  val RequestTimeoutResponse = Error("101", "Request timeout")

  case class WithProps(r: RequestContext, props: Props, message: RestWithHeader, p: Promise[RouteResult]) extends PerRequestActor {
    lazy val target: ActorRef = context.actorOf(props, "target")
  }
}

trait PerRequestCreator {
  def perRequest(r: RequestContext, props: Props, req: RestWithHeader, p: Promise[RouteResult])
                (implicit ac: ActorSystem): ActorRef =
    ac.actorOf(Props(classOf[WithProps], r, props, req, p), s"pr-${UUID.randomUUID().toString}")
}
