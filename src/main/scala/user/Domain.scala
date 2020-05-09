package user

trait RestMessage

case class CreateClient(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class UpdateClient(id: String, userName: String, mobile: String, password: String, rating: Int) extends RestMessage
case class DeleteClient(id: String) extends RestMessage
case class GetClient(id: String) extends RestMessage
case class GetClientToken(id: String, password: String) extends RestMessage
case class CheckClientToken(id: String, token: String) extends RestMessage

case class CreateAdmin(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class UpdateAdmin(id: String, userName: String, mobile: String, password: String) extends RestMessage
case class DeleteAdmin(id: String) extends RestMessage
case class GetAdmin(id: String) extends RestMessage

case class RestWithHeader(restMessage: RestMessage, header: Map[String, String])

object SuccessfulOperation
final case class Error(code: String, message: String)
final case class Accepted(code: String, message: String)
final case class User(userName: String,
                      password: String,
                      mobile: String,
                      rating: Int)

final case class ClientInfo(userId: String,
                            userName: String,
                            mobile: String,
                            rating: Int)
final case class TokenResponse(
                                statusCode: Int,
                                description: String,
                                jwtToken: Option[String] = None
                              )