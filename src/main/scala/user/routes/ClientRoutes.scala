package user.routes

import akka.actor.Props
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives.{entity, _}
import user._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

trait ClientRoutes extends BaseRoute {
  def someProps: Props

  //
  lazy val route: Route = Route {
    concat(
      pathPrefix("user"){
        concat(
          check(),
          create(),
          verify(),
          verificationCode(),
          update(),
          deleteUser(),
          pathPrefix("token") {
            concat(
              getTokenByEmail,
              getTokenByNikName
            )
          },
          getUser
        )
      }
    )
  }

  def check(): Route = {
    httpHeaders { headers =>
      parameter("check".as[String]) {
        case "email" =>
          entity(as[CheckEmail]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
        case "nikName" =>
          entity(as[CheckNikName]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
        case "token" =>
          concat(
            entity(as[CheckUserTokenByNikName]) { entity =>
              post {
                val cmd = RestWithHeader(entity, headers)
                work {
                  cmd
                }
              }
            },
            entity(as[CheckUserTokenByEmail]) { entity =>
              post {
                val cmd = RestWithHeader(entity, headers)
                work {
                  cmd
                }
              }
            },
          )
      }
    }
  }

  def create(): Route = {
    httpHeaders { headers =>
      entity(as[Register]) { entity =>
        post {
          val cmd = RestWithHeader(entity, headers)
          work {
            cmd
          }
        }
      }
    }
  }

  def verify(): Route = {
    httpHeaders { headers =>
      parameter("verify".as[String]) {
        case "email" =>
          entity(as[VerifyEmail]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
      }
    }
  }

  def verificationCode(): Route = {
    httpHeaders { headers =>
      parameter("verificationCode".as[String]) {
        case "email" =>
          entity(as[VerificationCode]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
        case "resendCode" =>
          entity(as[ResendEmailCode]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
      }
    }
  }

  def update(): Route = {
    httpHeaders { headers =>
      parameter("update".as[String]) {
        case "nikName" =>
          entity(as[UpdateNikName]) { entity =>
            post {
              val cmd = RestWithHeader(entity, headers)
              work {
                cmd
              }
            }
          }
      }
    }
  }

  def deleteUser(): Route = {
    httpHeaders { headers =>
      entity(as[DeleteClient]) { entity =>
        delete {
          val cmd = RestWithHeader(entity, headers)
          work {
            cmd
          }
        }
      }
    }
  }

  def getUser: Route = {
    httpHeaders { headers =>
      concat(
        entity(as[GetUserById]) { entity =>
          get {
            val cmd = RestWithHeader(entity, headers)
            work {
              cmd
            }
          }
        },
        entity(as[GetUserByEmail]) { entity =>
          get {
            val cmd = RestWithHeader(entity, headers)
            work {
              cmd
            }
          }
        },
        entity(as[GetUserByNikName]) { entity =>
          get {
            val cmd = RestWithHeader(entity, headers)
            work {
              cmd
            }
          }
        },
      )
    }
  }

  def getTokenByNikName: Route = {
    httpHeaders { headers =>
      entity(as[GetUserTokenByNikName]) { entity =>
        get {
          val cmd = RestWithHeader(entity, headers)
          work {
            cmd
          }
        }
      }
    }
  }

  def getTokenByEmail: Route = {
    httpHeaders { headers =>
      entity(as[GetUserTokenByEmail]) { entity =>
        get {
          val cmd = RestWithHeader(entity, headers)
          work {
            cmd
          }
        }
      }
    }
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
