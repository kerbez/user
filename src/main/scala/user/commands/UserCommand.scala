package user.commands

sealed trait UserCommand {

  type Identifier = String

  def userId: Identifier

}

object UserCommand {
  case class CreateClientCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateClientCommand(userId: String, userName: String, mobile: String, password: String, rating: Int, token: String) extends UserCommand
  case class DeleteClientCommand(userId: String, token: String) extends UserCommand
  case class GetClientCommand(userId: String) extends UserCommand
  case class GetClientTokenCommand(userId: String, password: String) extends UserCommand
  case class CheckClientTokenCommand(userId: String, token: String) extends UserCommand

  case class CreateAdminCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateAdminCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class DeleteAdminCommand(userId: String) extends UserCommand
  case class GetAdminCommand(userId: String) extends UserCommand
}
