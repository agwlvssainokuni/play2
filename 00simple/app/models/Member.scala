package models

import java.sql.Connection
import java.util.Date

import anorm._
import anorm.SqlParser._

case class Member(name: String, birthday: Option[Date], groupId: Int) {
  var id: Pk[Int] = NotAssigned
}

object Member {

  val parse = {
    int("members.id") ~ str("members.name") ~ (date("members.birthday")?) ~ int("members.group_id") map {
      case id ~ name ~ birthday ~ groupId => Member(Id(id), name, birthday, groupId)
    }
  }

  def apply(id: Pk[Int], name: String, birthday: Option[Date], groupId: Int): Member = {
    val member = Member(name, birthday, groupId)
    member.id = id
    member
  }

  def list()(implicit c: Connection): List[Member] =
    SQL("""
        SELECT id, name, birthday, group_id FROM members
        """).as(parse *)

  def find(id: Int)(implicit c: Connection): Option[Member] =
    SQL("""
        SELECT id, name, birthday, group_id FROM members WHERE id = {id}
        """).on(
      'id -> id).as(parse singleOpt)

  def findWithGroup(id: Int)(implicit c: Connection): Option[(Member, Option[Group])] =
    SQL("""
        SELECT
            A.id,
            A.name,
            A.birthday,
            A.group_id,
            B.id,
            B.name
        FROM
            members AS A
            LEFT OUTER JOIN
            groups AS B
            ON
              A.group_id = B.id
        WHERE
            A.id = {id}
        """).on(
      'id -> id).as((parse ~ (Group.parse ?) map {
        case mem ~ grp => (mem, grp)
      })*) headOption

  def create(member: Member)(implicit c: Connection) =
    SQL("""
        INSERT INTO members (
            name,
            birthday,
            group_id
        )
        VALUES (
            {name},
            {birthday},
            {groupId}
        )
        """).on(
      'name -> member.name, 'birthday -> member.birthday, 'groupId -> member.groupId).executeUpdate()

  def update(id: Int, member: Member)(implicit c: Connection) =
    SQL("""
        UPDATE members
        SET
            name = {name},
            birthday = {birthday},
            group_id = {groupId}
        WHERE
            id = {id}
        """).on(
      'id -> id, 'name -> member.name, 'birthday -> member.birthday, 'groupId -> member.groupId).executeUpdate()

  def delete(id: Int)(implicit c: Connection) =
    SQL("""
        DELETE FROM members WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()

}
