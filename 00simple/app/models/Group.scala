package models

import java.sql.Connection

import anorm._
import anorm.SqlParser._

case class Group(name: String) {
  var id: Option[Int] = None
}

object Group {

  val parse = {
    int("groups.id") ~ str("groups.name") map {
      case id ~ name =>
        val group = Group(name)
        group.id = Some(id)
        group
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

  def findWithMembers(id: Int)(implicit c: Connection): Option[(Group, List[Member])] =
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
          (grp, gmems.flatMap {
            case (_, Some(mem)) => List(mem)
            case _ => List()
          })
      } headOption

  def create(group: Group)(implicit c: Connection) =
    SQL("""
        INSERT INTO groups (name) VALUES ({name})
        """).on(
      'name -> group.name).executeUpdate()

  def update(id: Int, group: Group)(implicit c: Connection) =
    SQL("""
        UPDATE groups SET name = {name} WHERE id = {id}
        """).on(
      'id -> id, 'name -> group.name).executeUpdate()

  def delete(id: Int)(implicit c: Connection) =
    SQL("""
        DELETE FROM groups WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()

}
