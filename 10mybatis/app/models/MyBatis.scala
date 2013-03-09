package models

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy.newProxyInstance

import org.mybatis.scala.config._
import org.mybatis.scala.session._

import javax.sql.DataSource
import play.api.Play.current
import play.api.db.DB

object MyBatis {

  var dataSource = DB.getDataSource()

  private val config = Configuration(
    Environment(
      "default",
      new JdbcTransactionFactory(),
      newProxyInstance(
        classOf[DataSource].getClassLoader(),
        Array(classOf[DataSource]),
        new InvocationHandler() {
          def invoke(proxy: Any, method: Method, args: Array[Object]) =
            method.invoke(dataSource, args: _*)
        }).asInstanceOf[DataSource]))

  config ++= Group
  config ++= Member

  lazy val sessionManager = config.createPersistenceContext

}
