package user.model

import akka.http.scaladsl.model.DateTime

sealed trait UserEvent

object UserEvent{
  case class UserCreatedEvent(id: String, userName: String, mobile: String, password: String, timestamp: DateTime) extends UserEvent
  case class UserUpdatedEvent(id: String, userApplication: UserApplication, timestamp: DateTime) extends UserEvent
  case class UserDeletedEvent(id: String, timestamp: DateTime) extends UserEvent
}

