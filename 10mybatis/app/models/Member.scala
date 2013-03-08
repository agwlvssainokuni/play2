package models

import java.util.Date

import org.mybatis.scala.mapping._

case class Member(var id: Int, var name: String, var birthday: Option[Date], var groupId: Int) {
  var group: Group = null
}

object Member {

  val list = new SelectList[Member] {
    def xsql =
      <xsql>
        SELECT id, name, birthday, group_id FROM members
      </xsql>
  }

  val find = new SelectOneBy[Int, Member] {
    def xsql =
      <xsql>
        SELECT id, name, birthday, group_id FROM members WHERE id = #{{id}}
      </xsql>
  }

  val findWithGroup = new SelectOneBy[Int, Member] {

    resultMap = new ResultMap[Member] {
      idArg("id")
      arg("name")
      arg("birthday")
      arg("group_id")
      association[Group](property = "group",
        resultMap = new ResultMap[Group] {
          idArg("B_id")
          arg("B_name")
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
            A.id =#{{id}}
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
            #{{name}},
            #{{birthday}},
            #{{groupId}}
        )
      </xsql>
  }

  val update = new Update[Member] {
    def xsql =
      <xsql>
        UPDATE members
        SET
            name = #{{name}},
            birthday = #{{birthday}},
            group_id = #{{groupId}}
        WHERE
            id = #{{id}}
      </xsql>
  }

  val delete = new Delete[Int] {
    def xsql =
      <xsql>
        DELETE FROM members WHERE id = #{{id}}
      </xsql>
  }

  def bind = Seq(list, find, findWithGroup, create, update, delete)

}
