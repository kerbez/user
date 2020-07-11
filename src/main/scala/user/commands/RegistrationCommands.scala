package user.commands

trait RegistrationCommands

object RegistrationCommands {
  case class CheckEmailCommand(email: String) extends RegistrationCommands
  case class CheckNikNameCommand(nikName: String) extends RegistrationCommands
  case class RegisterCommand(nikName: String, password: String) extends RegistrationCommands
}
