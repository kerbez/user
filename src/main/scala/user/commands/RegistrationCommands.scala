package user.commands

trait RegistrationCommands

object RegistrationCommands {
  case class CheckEmailCommand(email: String) extends RegistrationCommands
  case class CheckNikNameCommand(nikName: String) extends RegistrationCommands
  case class RegisterCommand(email: String, password: String) extends RegistrationCommands
}
