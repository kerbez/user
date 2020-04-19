package user.entity

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion
import akka.http.scaladsl.model.DateTime
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import user.CreateClient
import user.commands.UserCommand
import user.commands.UserCommand.CreateClientCommand
import user.model.UserEvent.{UserCreatedEvent, UserDeletedEvent, UserUpdatedEvent}
import user.model.{State, UserData, UserEvent, UserStatus}

object UserEntity {
  def props = Props(new UserEntity())
//
//  val idExtractor: ShardRegion.ExtractEntityId = {
//    case cmd: UserCommand => (cmd.userId, cmd)
//  }
//
//  val shardResolver: ShardRegion.ExtractShardId = {
//    case cmd: UserCommand =>
//      (math.abs(cmd.userId.hashCode) % 100).toString
//  }
//
//  val shardName: String = "UserShard"
}

class UserEntity() extends Actor {

  def updateState(event: UserEvent) = {
    event match {
      case evt: UserCreatedEvent =>
        state = state.copy(
          data = state.data.copy(
            id = evt.id,
            application = state.data.application
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
        case UserStatus.INIT => context.become(receive)
//        case UserStatus.CREATED => context.become(created)
//        case UserStatus.UPDATED => context.become(updated)
//        case UserStatus.DELETED => context.become(deleted)
      }
    case evt: UserEvent  =>
      updateState(evt)
  }

//  override def receiveCommand: Receive = init

  def receive: Receive = {
    case cmd: CreateClientCommand =>
      println(s"Got cmd: $cmd")
      val event = UserCreatedEvent(cmd.userId, cmd.userName, cmd.mobile, cmd.password, DateTime.now)
      sender() ! "created"
    case any =>
      println(s"Got any $any")

  }
//  def created: Receive = {
//
//  }
//  def updated: Receive = {
//
//  }
//  def deleted: Receive = {
//
//  }

}
