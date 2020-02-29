package user.routes

import akka.actor.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

trait ClientRoutes {
  def someActor: ActorRef

  lazy val route = Route {
    pathPrefix("hi"){
      concat(
        path("world"){
          get{
//            implicit val timeout: Timeout = 5.seconds
//
//            // query the actor for the current auction state
//            val bids: Future[Bids] = (auction ? GetBids).mapTo[Bids]
//            complete(bids)

            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
          }
        }
      )
    }
  }


}
