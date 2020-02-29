package user.model

sealed trait UserApplication

object UserApplication{
  case class ClientApplication(
                              id: String,
                              name: String,
                              userName: String,
                              password: String,
                              mobile: String,
                              rating: Int
                              ) extends UserApplication

  case class AdminApplication(
                               id: String,
                               name: String,
                               userName: String,
                               password: String,
                               mobile: String,
                               permissionLevel: Int
                             ) extends UserApplication
}