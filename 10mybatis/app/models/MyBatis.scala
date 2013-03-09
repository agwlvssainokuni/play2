package models

import org.mybatis.scala.config._
import org.mybatis.scala.session._
import play.api.Play
import play.api.db.DB
import javax.sql.DataSource

object MyBatis {

  implicit var application = Play.current

  val config = Configuration(
    Environment(
      "default",
      new JdbcTransactionFactory(),
      new DataSource() {
        private def dataSource = DB.getDataSource()
        def getConnection() = dataSource.getConnection()
        def getConnection(u: String, p: String) = dataSource.getConnection(u, p)
        def getLogWriter() = dataSource.getLogWriter()
        def setLogWriter(out: java.io.PrintWriter) = dataSource.setLogWriter(out)
        def setLoginTimeout(seconds: Int) = dataSource.setLoginTimeout(seconds)
        def getLoginTimeout() = dataSource.getLoginTimeout()
        def getParentLogger() = dataSource.getParentLogger()
        def unwrap[T](iface: Class[T]) = dataSource.unwrap(iface)
        def isWrapperFor(iface: Class[_]) = dataSource.isWrapperFor(iface)
      }))

  config ++= Group
  config ++= Member

  lazy val sessionManager = config.createPersistenceContext

  def withConnection[T](callback: (Session) => T) =
    sessionManager.managed(callback)

  def withTransaction[T](callback: (Session) => T) =
    sessionManager.transaction(callback)

}
