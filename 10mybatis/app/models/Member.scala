package models

import java.util.Date

import org.mybatis.scala.mapping._
import org.mybatis.scala.mapping.Binding._

case class Member(var id: Int, var name: String, var birthday: Option[Date], var groupId: Int) {
  var group: Group = null
}

object Member {

  val list = new SelectList[Member] {

    resultMap = new ResultMap[Member] {
      idArg("id", T[Int])
      arg("name", T[String])
      arg("birthday", T[Option[Date]], JdbcType.DATE)
      arg("group_id", T[Int])
    }

    def xsql =
      <xsql>
        SELECT id, name, birthday, group_id FROM members
      </xsql>
  }

  val find = new SelectOneBy[Int, Member] {

    resultMap = new ResultMap[Member] {
      idArg("id", T[Int])
      arg("name", T[String])
      arg("birthday", T[Option[Date]], JdbcType.DATE)
      arg("group_id", T[Int])
    }

    def xsql =
      <xsql>
        SELECT id, name, birthday, group_id FROM members WHERE id ={ ?[Int]("id") }
      </xsql>
  }

  val findWithGroup = new SelectOneBy[Int, Member] {

    resultMap = new ResultMap[Member] {
      idArg("id", T[Int])
      arg("name", T[String])
      arg("birthday", T[Option[Date]], JdbcType.DATE)
      arg("group_id", T[Int])
      association[Group](property = "group",
        resultMap = new ResultMap[Group] {
          idArg("B_id", T[Int])
          arg("B_name", T[String])
        })
    }

    def xsql =
      <xsql>
        SELECT
            A.id,
            A.name,
            A.birthday,
            A.group_id,
            B.id		AS B_id,
            B.name		AS B_name
        FROM
            members AS A
            LEFT OUTER JOIN
            groups AS B
            ON
              A.group_id = B.id
        WHERE
            A.id ={ ?[Int]("id") }
      </xsql>
  }

  val create = new Insert[Member] {
    def xsql =
      <xsql>
        INSERT INTO members (
            name,
            birthday,
            group_id
        )
        VALUES (
            { ?[String]("name") },
            { ?[Option[Date]]("birthday", JdbcType.DATE) },
            { ?[Int]("groupId") }
        )
      </xsql>
  }

  val update = new Update[Member] {
    def xsql =
      <xsql>
        UPDATE members
        SET
            name ={ ?[String]("name") },
            birthday ={ ?[Option[Date]]("birthday", JdbcType.DATE) },
            group_id ={ ?[Int]("groupId") }
        WHERE
            id ={ ?[Int]("id") }
      </xsql>
  }

  val delete = new Delete[Int] {
    def xsql =
      <xsql>
        DELETE FROM members WHERE id ={ ?[Int]("id") }
      </xsql>
  }

  def bind = Seq(list, find, findWithGroup, create, update, delete)

}
