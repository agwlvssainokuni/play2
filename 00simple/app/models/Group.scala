package models

import java.sql.Connection

import anorm._
import anorm.SqlParser._

case class Group(id: Int, name: String) {
  var members: List[Member] = List()
}

object Group {

  val parse = {
    get[Int]("groups.id") ~ get[String]("groups.name") map {
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
      'id -> id).as(parse.singleOpt)

  def findWithMembers(id: Int)(implicit c: Connection): Option[Group] =
    find(id) match {
      case Some(group) =>
        group.members = Member.listByGroupId(id)
        Some(group)
      case None =>
        None
    }

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
