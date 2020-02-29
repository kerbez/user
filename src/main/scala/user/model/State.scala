package user.model

import akka.http.scaladsl.model.DateTime
import user.model.UserApplication.ClientApplication

case class State(data: UserData, status: UserStatus, timestamp: DateTime)

object State{
  def empty() = {
    State(UserData("", ClientApplication("","","", "", "", 0)), UserStatus.INIT, DateTime.now)
  }
}