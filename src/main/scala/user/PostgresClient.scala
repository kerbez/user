package user

import akka.stream.Materializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContextExecutor, Future}
object PostgresClient{
  class Users(tag: Tag)
    extends Table[(String, String, String, String, Boolean, Boolean, String, Int, String)](
      tag,
      Some("test_user_schema"),
      "test_user2"
    ) {
    def id          = column[String]("id")
    def nikName     = column[String]("nikName")
    def password    = column[String]("password")
    def email       = column[String]("email")
    def emailVerified = column[Boolean]("emailVerified")
    def mobileVerified = column[Boolean]("mobileVerified")
    def name        = column[String]("name")
    def rating      = column[Int]("rating")
    def mobile      = column[String]("mobile")
    def * =
      (id, nikName, password, email, emailVerified, mobileVerified, name, rating, mobile)
    def pk = primaryKey("test_user_pkey", mobile)
  }

  val users = TableQuery[Users]
}

case class PostgresClient()(implicit session: SlickSession, executionContext: ExecutionContextExecutor, materializer: Materializer) {
  val databaseConfig = DatabaseConfig.forConfig[JdbcProfile]("slick-postgres")
  import PostgresClient._
  import session.profile.api._

  def find(id: String): Future[Option[(String, String, String, String, Boolean, Boolean, String, Int, String)]] = {
    databaseConfig.db
      .run((for (user <- users if user.id === id) yield user).result.headOption)
  }

  def findEmail(email: String): Future[Option[(String, String, String, String, Boolean, Boolean, String, Int, String)]] = {
    databaseConfig.db
      .run((for (user <- users if user.email === email) yield user).result.headOption)
  }

  def findNikName(nikName: String): Future[Option[(String, String, String, String, Boolean, Boolean, String, Int, String)]] = {
    databaseConfig.db
      .run((for (user <- users if user.nikName === nikName) yield user).result.headOption)
  }

  def insert(id: String, nikname: String, password: String, rating: Int): Future[Int] =
    databaseConfig.db
      .run(users += (id, nikname, password, "", false, false, "", rating, ""))

//  def updateClient(id: String, nikName: Option[String], password: Option[String], email: Option[String], emailVerified: Option[Boolean], rating: Option[Int]): Future[Boolean] = {
//    val query = for (user <- users if user.id === id)
//      yield (user.nikName, user.password, user.email, user.emailVerified, user.rating)
//    databaseConfig.db.run(query.update(nikName.getOrElse(name), password, rating)) map { _ > 0 }
//  }

  def updateVerifiedEmail(id: String, email: String, emailVerified: Boolean): Future[Boolean] = {
    val query = for (user <- users if user.id === id)
      yield (user.email, user.emailVerified)
    databaseConfig.db.run(query.update(email, emailVerified)) map { _ > 0 }

  }

  def updateName(id: String, name: String): Future[Boolean] = {
    val query = for (user <- users if user.id === id)
      yield user.name
    databaseConfig.db.run(query.update(name)) map { _ > 0 }
  }

  def updatePassword(id: String, password: String): Future[Boolean] = {
    val query = for (user <- users if user.id === id)
      yield user.password
    databaseConfig.db.run(query.update(password)) map { _ > 0 }
  }

  def updateRating(id: String, rating: Int): Future[Boolean] = {
    val query = for (user <- users if user.id === id)
      yield user.rating
    databaseConfig.db.run(query.update(rating)) map { _ > 0 }
  }

  def delete(id: String): Future[Boolean] =
    databaseConfig.db.run(users.filter(_.id === id).delete) map { _ > 0 }

}
