package models

import java.util.Date

import org.mybatis.scala.mapping._
import org.mybatis.scala.mapping.Binding._

case class Group(var id: Int, var name: String) {
  var members: Seq[Member] = null
}

object Group {

  val list = new SelectList[Group] {

    resultMap = new ResultMap[Group] {
      idArg("id", T[Int])
      arg("name", T[String])
    }

    def xsql =
      <xsql>
        SELECT id, name FROM groups
      </xsql>
  }

  val find = new SelectOneBy[Int, Group] {

    resultMap = new ResultMap[Group] {
      idArg("id", T[Int])
      arg("name", T[String])
    }

    def xsql =
      <xsql>
        SELECT id, name FROM groups WHERE id ={ ?[Int]("id") }
      </xsql>
  }

  val findWithMembers = new SelectOneBy[Int, Group] {

    resultMap = new ResultMap[Group] {
      idArg("id", T[Int])
      arg("name", T[String])
      collection[Member](property = "members",
        resultMap = new ResultMap[Member] {
          idArg("B_id", T[Int])
          arg("B_name", T[String])
          arg("B_birthday", T[Option[Date]], JdbcType.DATE)
          arg("B_group_id", T[Int])
        })
    }

    def xsql =
      <xsql>
        SELECT
            A.id,
            A.name,
            B.id		AS B_id,
            B.name		AS B_name,
            B.birthday	AS B_birthday,
            B.group_id	AS B_group_id
        FROM
            groups AS A
            LEFT OUTER JOIN
            members AS B
            ON
              A.id = B.group_id
        WHERE
            A.id ={ ?[Int]("id") }
      </xsql>
  }

  val create = new Insert[Group] {
    def xsql =
      <xsql>
        INSERT INTO groups (name) VALUES ({ ?[String]("name") })
      </xsql>
  }

  val update = new Update[Group] {
    def xsql =
      <xsql>
        UPDATE groups SET name ={ ?[String]("name") } WHERE id ={ ?[Int]("id") }
      </xsql>
  }

  val delete = new Delete[Int] {
    def xsql =
      <xsql>
        DELETE FROM groups WHERE id ={ ?[Int]("id") }
      </xsql>
  }

  def bind = Seq(list, find, findWithMembers, create, update, delete)

}
