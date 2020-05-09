package user.routes

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.{Route, RouteResult}
import user.{JsonSupport, RestMessage, RestWithHeader}

import scala.concurrent.Promise

trait BaseRoute extends PerRequestCreator with JsonSupport {
  def actorSys: ActorSystem

  def handleRequest(targetProps: Props, request: RestWithHeader): Route = ctx => {
    val p = Promise[RouteResult]
    println("doing here smth")
    perRequest(ctx, targetProps, request, p)(actorSys)
    p.future
  }
}