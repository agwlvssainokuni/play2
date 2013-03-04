package models

import java.sql.Connection

import anorm._
import anorm.SqlParser._

case class Group(id: Int, name: String) {
  var members: List[Member] = List()
}

object Group {

  val parse = {
    int("groups.id") ~ str("groups.name") map {
      case id ~ name => Group(id, name)
    }
  }

  def list()(implicit c: Connection): List[Group] =
    SQL("""
        SELECT id, name FROM groups
        """).as(parse *)

  def find(id: Int)(implicit c: Connection): Option[Group] =
    SQL("""
        SELECT id, name FROM groups WHERE id = {id}
        """).on(
      'id -> id).as(parse singleOpt)

  def findWithMembers(id: Int)(implicit c: Connection): Option[Group] =
    SQL("""
        SELECT
            A.id,
            A.name,
            B.id,
            B.name,
            B.birthday,
            B.group_id
        FROM
            groups AS A
            LEFT OUTER JOIN
            members AS B
            ON
              A.id = B.group_id
        WHERE
            A.id = {id}
        """).on(
      'id -> id).as((parse ~ (Member.parse ?) map {
        case grp ~ mem => (grp, mem)
      })*) groupBy {
        case (grp, _) => grp
      } map {
        case (grp, gmems) =>
          grp.members = gmems.flatMap {
            case (_, Some(mem)) =>
              mem.group = Option(grp)
              List(mem)
            case _ =>
              List()
          }
          grp
      } headOption

  def create(name: String)(implicit c: Connection) =
    SQL("""
        INSERT INTO groups (name) VALUES ({name})
        """).on(
      'name -> name).executeUpdate()

  def update(id: Int, name: String)(implicit c: Connection) =
    SQL("""
        UPDATE groups SET name = {name} WHERE id = {id}
        """).on(
      'id -> id, 'name -> name).executeUpdate()

  def delete(id: Int)(implicit c: Connection) =
    SQL("""
        DELETE FROM groups WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()

}
