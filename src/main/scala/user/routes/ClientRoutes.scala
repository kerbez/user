package user.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{entity, _}
import user._

trait ClientRoutes extends BaseRoute {
  def someProps: Props

  //
  lazy val route = Route {
    concat(
      pathPrefix("client"){
        concat(
          path("create"){
            entity(as[CreateClient]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("update"){
            entity(as[UpdateClient]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("delete"){
            entity(as[DeleteClient]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("get"){
            entity(as[GetClient]){ entity =>
              post{
                work{entity}
              }
            }
          }
        )
      },
      pathPrefix("admin"){
        concat(
          path("create"){
            entity(as[CreateAdmin]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("update"){
            entity(as[UpdateAdmin]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("delete"){
            entity(as[DeleteAdmin]){ entity =>
              post{
                work{entity}
              }
            }
          },
          path("get"){
            entity(as[GetAdmin]){ entity =>
              post{
                work{entity}
              }
            }
          }
        )
      }
    )
  }

  def work(cmd: RestMessage): Route = {
    handleRequest(someProps, cmd)
  }


}
