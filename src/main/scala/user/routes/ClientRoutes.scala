package user.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import user.{CreateAdmin, CreateClient, DeleteClient, RestMessage, UpdateClient}

trait ClientRoutes extends BaseRoute {
  def someProps: Props

  //
  lazy val route = Route {
    concat(
      pathPrefix("healthCheck"){
        get{
          complete("ok")
        }
      },
      pathPrefix("create"){
        concat(
          path("client"){
            entity(as[CreateClient]){ entity =>
              post{
                work{entity}
              }
            }
          }
          ,
          path("admin"){
            entity(as[CreateAdmin]){ entity =>
              post{
                work{entity}
              }
            }
          }
        )
      }
      ,
      pathPrefix("update"){
        entity(as[UpdateClient]){ entity =>
          post{
            work{entity}
          }
        }
      },
      pathPrefix("delete"){
        entity(as[DeleteClient]){ entity =>
          post{
            work{entity}
          }
        }
      }
    )
  }

  def work(cmd: RestMessage) = {
    handleRequest(someProps, cmd)
  }


}
