package user.service

import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import user.secvice.UserEntity.User
import slick.jdbc.PostgresProfile.api._
import user.Json4sSerializer

import scala.concurrent.{ExecutionContextExecutor, Future}
object PostgresClient{
  class Users(tag: Tag)
    extends Table[(String, String, String, Int)](
      tag,
      Some("test_user_schema"),
      "test_user2"
    ) {
    def mobile      = column[String]("mobile")
    def name     = column[String]("name")
    def password           = column[String]("password")
    def rating        = column[Int]("rating")

    def * =
      (mobile, name, password, rating)
    def pk = primaryKey("test_user_pkey", mobile)
  }

  val users = TableQuery[Users]
}

case class PostgresClient()(implicit session: SlickSession, executionContext: ExecutionContextExecutor, materializer: Materializer) extends Json4sSerializer{
  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")
  import session.profile.api._
  import PostgresClient._

  def find(mobile: String): Future[Option[(String, String, String, Int)]] = {
    databaseConfig.db.run((for (user <- users if user.mobile === mobile) yield user).result.headOption)
  }

  def insert(mobile: String, name: String, password: String, rating: Int): Future[Int] = databaseConfig.db.run(users += (mobile, name, password, rating))

  def update(mobile: String, name: String, password: String, rating: Int): Future[Boolean] = {
    val query = for (user <- users if user.mobile === mobile)
      yield (user.name, user.password, user.rating)
    databaseConfig.db.run(query.update(name, password, rating)) map { _ > 0 }
  }

  def delete(mobile: String): Future[Boolean] =
    databaseConfig.db.run(users.filter(_.mobile === mobile).delete) map { _ > 0 }

}
