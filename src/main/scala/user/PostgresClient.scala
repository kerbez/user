package user

import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContextExecutor, Future}
object PostgresClient{
  class Users(tag: Tag)
    extends Table[(String, String, String, Int)](
      tag,
      Some("test_user_schema"),
      "test_user2"
    ) {
    def mobile      = column[String]("mobile")
    def name        = column[String]("name")
    def password    = column[String]("password")
    def rating      = column[Int]("rating")
    def * =
      (mobile, name, password, rating)
    def pk = primaryKey("test_user_pkey", mobile)
  }

  val users = TableQuery[Users]
}

case class PostgresClient()(implicit session: SlickSession, executionContext: ExecutionContextExecutor, materializer: Materializer) {
  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")
  import PostgresClient._
  import session.profile.api._

  def find(mobile: String): Future[Option[(String, String, String, Int)]] = {
    databaseConfig.db
      .run((for (user <- users if user.mobile === mobile) yield user).result.headOption)
  }

  def insert(mobile: String, name: String, password: String, rating: Int): Future[Int] =
    databaseConfig.db
      .run(users += (mobile, name, password, rating))

  def updateClient(mobile: String, name: String, password: String, rating: Int): Future[Boolean] = {
    val query = for (user <- users if user.mobile === mobile)
      yield (user.name, user.password, user.rating)
    databaseConfig.db.run(query.update(name, password, rating)) map { _ > 0 }
  }

  def updateName(mobile: String, name: String): Future[Boolean] = {
    val query = for (user <- users if user.mobile === mobile)
      yield user.name
    databaseConfig.db.run(query.update(name)) map { _ > 0 }
  }
  def updatePassword(mobile: String, password: String): Future[Boolean] = {
    val query = for (user <- users if user.mobile === mobile)
      yield user.password
    databaseConfig.db.run(query.update(password)) map { _ > 0 }
  }
  def updateRating(mobile: String, rating: Int): Future[Boolean] = {
    val query = for (user <- users if user.mobile === mobile)
      yield user.rating
    databaseConfig.db.run(query.update(rating)) map { _ > 0 }
  }

  def delete(mobile: String): Future[Boolean] =
    databaseConfig.db.run(users.filter(_.mobile === mobile).delete) map { _ > 0 }

}
