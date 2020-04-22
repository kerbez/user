package user.model

import akka.http.scaladsl.model.DateTime
import user.model.UserApplication.ClientApplication
import user.model.UserEvent.{ClientCreatedEvent, UserDeletedEvent, UserUpdatedEvent}

case class State(data: UserData, status: UserStatus, timestamp: DateTime){
//  def empty() = {
//    State(UserData("", ClientApplication("","","", "", "", 0)), UserStatus.INIT, DateTime.now)
//  }

  def updateState(event: UserEvent): State = {
    event match {
      case evt: ClientCreatedEvent =>
        copy(
          data = data.copy(
            id = evt.id,
            application = data.application
          ),
          status = UserStatus.CREATED,
          timestamp = evt.timestamp
        )
      case evt: UserUpdatedEvent =>
        copy(
          data = data.copy(
            application = evt.userApplication),
          status = UserStatus.UPDATED
        )
      case evt: UserDeletedEvent =>
        copy(
          status = UserStatus.DELETED,
          timestamp = evt.timestamp
        )
    }
  }
}