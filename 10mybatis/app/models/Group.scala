package models

import org.mybatis.scala.mapping._

case class Group(var id: Int, var name: String) {
  var members: Seq[Member] = null
}

object Group {

  val list = new SelectList[Group] {
    def xsql =
      <xsql>
        SELECT id, name FROM groups
      </xsql>
  }

  val find = new SelectOneBy[Int, Group] {
    def xsql =
      <xsql>
        SELECT id, name FROM groups WHERE id =#{{id}}
      </xsql>
  }

  val findWithMembers = new SelectOneBy[Int, Group] {

    resultMap = new ResultMap[Group] {
      idArg("id")
      arg("name")
      collection[Member](property = "members",
        resultMap = new ResultMap[Member] {
          idArg("B_id")
          arg("B_name")
          arg("B_birthday")
          arg("B_group_id")
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
            A.id =#{{id}}
      </xsql>
  }

  val create = new Insert[Group] {
    def xsql =
      <xsql>
        INSERT INTO groups (name) VALUES (#{{name}})
      </xsql>
  }

  val update = new Update[Group] {
    def xsql =
      <xsql>
        UPDATE groups SET name = #{{name}} WHERE id = #{{id}}
      </xsql>
  }

  val delete = new Delete[Int] {
    def xsql =
      <xsql>
        DELETE FROM groups WHERE id = #{{id}}
      </xsql>
  }

  def bind = Seq(list, find, findWithMembers, create, update, delete)

}
