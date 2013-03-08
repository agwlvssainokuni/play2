package models

import org.mybatis.scala.config._
import org.mybatis.scala.session._

import play.api.Play.current
import play.api.db.DB._

object MyBatis {

  val config = Configuration(
    Environment(
      "default",
      new JdbcTransactionFactory(),
      getDataSource()))

  config ++= Group
  config ++= Member

  lazy val sessionManager = config.createPersistenceContext

  def withConnection[T](callback: (Session) => T) =
    sessionManager.transaction(callback)

  def withTransaction[T](callback: (Session) => T) =
    sessionManager.transaction(callback)

}
