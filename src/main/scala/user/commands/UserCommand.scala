package user.commands

sealed trait UserCommand {

  type Identifier = String

  def userId: Identifier

}

object UserCommand {
  case class CreateClientCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateClientCommand(userId: String, userName: String, mobile: String, password: String, rating: Int) extends UserCommand
  case class DeleteClientCommand(userId: String) extends UserCommand
  case class GetClientCommand(userId: String) extends UserCommand
  case class CreateAdminCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateAdminCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class DeleteAdminCommand(userId: String) extends UserCommand
  case class GetAdminCommand(userId: String) extends UserCommand
}
