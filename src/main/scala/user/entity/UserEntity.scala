package user.entity

import akka.actor.{Actor, Props}
import akka.http.scaladsl.model.DateTime
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import user.commands.UserCommand.CreateClient
import user.model.UserEvent.{UserCreatedEvent, UserDeletedEvent, UserUpdatedEvent}
import user.model.{State, UserData, UserEvent, UserStatus}

object UserEntity {
  def props = Props(new UserEntity())
}

class UserEntity() extends PersistentActor{

  def updateState(event: UserEvent) = {
    event match {
      case evt: UserCreatedEvent =>
        state = state.copy(
          data = state.data.copy(
            id = evt.id,
            application = state.data.application.copy
          ),
          status = UserStatus.CREATED,
          timestamp = evt.timestamp
        )
      case evt: UserUpdatedEvent =>
        state = state.copy(
          data = state.data.copy(
            application = evt.userApplication),
          status = UserStatus.UPDATED
        )
      case evt: UserDeletedEvent =>
        state = state.copy(
          status = UserStatus.DELETED,
          timestamp = evt.timestamp
        )
    }
  }

  var state: State = State.empty()

  val receiveRecover: Receive = {
    case RecoveryCompleted =>
      state.status match {
        case UserStatus.INIT => context.become(init)
        case UserStatus.CREATED => context.become(created)
        case UserStatus.UPDATED => context.become(updated)
        case UserStatus.DELETED => context.become(deleted)
      }
    case evt: UserEvent  =>
      updateState(evt)
  }

  override def receiveCommand: Receive = init

  def init: Receive = {
    case cmd: CreateClient =>
      val event = UserCreatedEvent(cmd.id, cmd.userName, cmd.mobile, cmd.password, DateTime.now)

  }
  def created: Receive = {

  }
  def updated: Receive = {

  }
  def deleted: Receive = {

  }

}
