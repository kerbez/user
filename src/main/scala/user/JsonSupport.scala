package user

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}
import user.entity.UserEntity.TokenResponse

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

//  implicit val createClientCommand = jsonFormat4(UserCommand.CreateClient)
  implicit val createClient = jsonFormat4(CreateClient)
  implicit val createAdmin = jsonFormat4(CreateAdmin)
  implicit val updateClient = jsonFormat4(UpdateClient)
  implicit val deleteClient = jsonFormat4(DeleteClient)

  implicit val tokenResponse = jsonFormat3(TokenResponse)
  implicit val errorFormat = jsonFormat1(Error)
  implicit val validationFormat = jsonFormat1(Validation)
  implicit val notExistFormat = jsonFormat1(NotExist)


  //  implicit object UserWithIdFormat extends RootJsonFormat[UserWithId] {
  //    override def read(json: JsValue): UserWithId = {
  //      throw new RuntimeException("Not implemented")
  //    }
  //    override def write(obj: UserWithId): JsValue = userFormat.write(obj.user)
  //  }
}
