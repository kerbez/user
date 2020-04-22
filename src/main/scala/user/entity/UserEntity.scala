package user.entity

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.DateTime
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import user.CreateClient
import user.commands.UserCommand
import user.commands.UserCommand.CreateClientCommand
import user.model.UserApplication.ClientApplication
import user.model.UserEvent.{ClientCreatedEvent, UserDeletedEvent, UserUpdatedEvent}
import user.model.{State, UserData, UserEvent, UserStatus}

object UserEntity {
  def props = Props(new UserEntity())

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: UserCommand => (cmd.userId, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: UserCommand =>
      (math.abs(cmd.userId.hashCode) % 100).toString
  }

  val shardName: String = "UserShard"
}

class UserEntity() extends PersistentActor {

  val log: LoggingAdapter = Logging(context.system, this)

  override def persistenceId: String = self.path.name

  var state: State = State(UserData(persistenceId, ClientApplication(persistenceId,"","", "", "", 0)), UserStatus.INIT, DateTime.now)

  val receiveRecover: Receive = {
    case RecoveryCompleted =>
      state.status match {
        case UserStatus.INIT => context.become(init)
        case UserStatus.CREATED => context.become(created)
        case UserStatus.UPDATED => context.become(updated)
        case UserStatus.DELETED => context.become(deleted)
      }
    case evt: UserEvent  =>
      state.updateState(evt)
  }

  override def receiveCommand: Receive = init

  def init: Receive = {
    case cmd: CreateClientCommand =>
      log.info(s"[init] Got CreateClientCommand: $cmd")
      val event = ClientCreatedEvent(cmd.userId, cmd.userName, cmd.mobile, cmd.password, DateTime.now)

      persist(event){ evt =>
        log.info(s"Event persisted: $evt")
        state = state.updateState(event)
        log.info(s"new state after persistence: $state")
        sender() ! "created"
        context.become(created)
      }
    case any =>
      log.info(s"Got any: $any")
      println(s"Got any $any")

  }
  def created: Receive = {
    case any=>
      log.info(s"[created] Got any: $any")
  }
  def updated: Receive = {
    case any=>
      log.info(s"[updated] Got any: $any")
  }
  def deleted: Receive = {
    case any=>
      log.info(s"[deleted] Got any: $any")
  }

}
