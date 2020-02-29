package user.model

sealed trait UserStatus

object UserStatus{
  case object INIT extends UserStatus
  case object CREATED extends UserStatus
  case object UPDATED extends UserStatus
  case object DELETED extends UserStatus
}