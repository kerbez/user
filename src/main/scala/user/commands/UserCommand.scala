package user.commands

sealed trait UserCommand

object UserCommand {
  case class CreateClient(id: String, userName: String, mobile: String, password: String) extends UserCommand
  case class UpdateClient() extends UserCommand
  case class DeleteClient() extends UserCommand
  case class CreateAdmin() extends UserCommand
}
