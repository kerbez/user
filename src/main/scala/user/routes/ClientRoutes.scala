package user.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader}
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives.{entity, _}
import user._

trait ClientRoutes extends BaseRoute {
  def someProps: Props

  //
  lazy val route = Route {
    concat(
      pathPrefix("client"){
        concat(
          httpHeaders { headers =>
            path("create") {
              entity(as[CreateClient]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          },
          httpHeaders { headers =>
            path("update") {
              entity(as[UpdateClient]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          },
          httpHeaders { headers =>
            path("delete") {
              entity(as[DeleteClient]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          },
          httpHeaders { headers =>
            path("get") {
              entity(as[GetClient]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          },
          httpHeaders { headers =>
            path("getToken") {
              entity(as[GetClientToken]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          },
          httpHeaders { headers =>
            path("checkToken") {
              entity(as[CheckClientToken]) { entity =>
                post {
                  val cmd = RestWithHeader(entity, headers)
                  work {
                    cmd
                  }
                }
              }
            }
          }
        )
      }
//      pathPrefix("admin"){
//        concat(
//          path("create"){
//            entity(as[CreateAdmin]){ entity =>
//              post{
//                work{entity}
//              }
//            }
//          },
//          path("update"){
//            entity(as[UpdateAdmin]){ entity =>
//              post{
//                work{entity}
//              }
//            }
//          },
//          path("delete"){
//            entity(as[DeleteAdmin]){ entity =>
//              post{
//                work{entity}
//              }
//            }
//          },
//          path("get"){
//            entity(as[GetAdmin]){ entity =>
//              post{
//                work{entity}
//              }
//            }
//          }
//        )
//      }
    )
  }

  def work(cmd: RestWithHeader): Route = {
    println(s"work GOT $cmd")
    handleRequest(someProps, cmd)
  }

  private def getHeaders(headers: Seq[HttpHeader]): Map[String, String] =
    headers.map { x =>
      (x.name, x.value)
    }.toMap

  private def httpHeaders: Directive1[Map[String, String]] = extract(c => getHeaders(c.request.headers))

}
