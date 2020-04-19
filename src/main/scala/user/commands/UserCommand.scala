package user.commands

sealed trait UserCommand {

  type Identifier = String

  def userId: Identifier

}

object UserCommand {
  case class CreateClientCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateClientCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class DeleteClientCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
  case class CreateAdminCommand(userId: String, userName: String, mobile: String, password: String) extends UserCommand
}
