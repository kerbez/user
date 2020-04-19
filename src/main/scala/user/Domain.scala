package user

trait RestMessage

case class CreateClient(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class UpdateClient(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class DeleteClient(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class CreateAdmin(id: String, userName: String, mobile: String, password: String) extends RestMessage



object SuccessfulOperation
final case class Validation(msg: String)
final case class Error(msg: String)
final case class NotExist(msg: String)