package user

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

//  implicit val createClientCommand = jsonFormat4(UserCommand.CreateClient)
  implicit val createClient = jsonFormat4(CreateClient)
  implicit val updateClient = jsonFormat5(UpdateClient)
  implicit val deleteClient = jsonFormat1(DeleteClient)
  implicit val getClient    = jsonFormat1(GetClient)
  implicit val getClientToken = jsonFormat2(GetClientToken)
  implicit val checkClientToken = jsonFormat2(CheckClientToken)

  implicit val createAdmin  = jsonFormat4(CreateAdmin)
  implicit val updateAdmin  = jsonFormat4(UpdateAdmin)
  implicit val deleteAdmin  = jsonFormat1(DeleteAdmin)
  implicit val getAdmin    = jsonFormat1(GetAdmin)

  implicit val tokenResponseFormat = jsonFormat3(TokenResponse)
  implicit val errorFormat = jsonFormat2(Error)
  implicit val clientInfoFormat = jsonFormat4(ClientInfo)
  implicit val acceptedFormat = jsonFormat2(Accepted)


  //  implicit object UserWithIdFormat extends RootJsonFormat[UserWithId] {
  //    override def read(json: JsValue): UserWithId = {
  //      throw new RuntimeException("Not implemented")
  //    }
  //    override def write(obj: UserWithId): JsValue = userFormat.write(obj.user)
  //  }
}
