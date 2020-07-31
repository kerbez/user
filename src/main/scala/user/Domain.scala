package user

trait RestMessage

//case class CreateClient(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class UpdateClient(id: String, userName: String, mobile: String, password: String, rating: Int) extends RestMessage
case class UpdateNikName(id: String, nikName: String, token: String) extends RestMessage
case class DeleteClient(id: String) extends RestMessage
case class GetUserById(id: String) extends RestMessage
case class GetUserByEmail(email: String) extends RestMessage
case class GetUserByNikName(nikName: String) extends RestMessage
case class GetUserTokenByNikName(nikName: String, password: String) extends RestMessage
case class GetUserTokenByEmail(email: String, password: String) extends RestMessage
case class CheckUserTokenByNikName(nikName: String, token: String) extends RestMessage
case class CheckUserTokenByEmail(email: String, token: String) extends RestMessage
case class CheckEmail(email: String) extends RestMessage
case class CheckNikName(nikName: String) extends RestMessage
case class Register(email: String, password: String) extends RestMessage
case class VerifyEmail(nikName: String, email: String) extends RestMessage
case class VerificationCode(id: String, code: String) extends RestMessage
case class ResendEmailCode(id: String) extends RestMessage

case class CreateAdmin(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class UpdateAdmin(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class DeleteAdmin(id: String) extends RestMessage
case class GetAdmin(id: String) extends RestMessage

case class RestWithHeader(restMessage: RestMessage, header: Map[String, String])

object SuccessfulOperation
final case class Error(code: String, message: String)
final case class Accepted(code: String, message: String)
final case class CodeSent(userId: String, message: String)
final case class GeneralResponse(code: String, message: String)
final case class User(userId: String,
                      nikName: String,
                       password: String,
                       email: Option[String],
                       rating: Int)

final case class ClientInfo(userId: String,
                            nikName: String,
                            email: String,
                            rating: Int)
final case class TokenResponse(
                                code: Int,
                                description: String,
                                jwtToken: Option[String] = None
                              )