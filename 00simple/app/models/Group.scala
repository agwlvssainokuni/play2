package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._

case class Group(id: Int, name: String, var members: List[Member])

object Group {

  private val parse = {
    get[Int]("id") ~ get[String]("name") map {
      case id ~ name => Group(id, name, List())
    }
  }

  def list(): List[Group] = DB.withConnection { implicit c =>
    SQL("""
        SELECT id, name FROM groups
        """).as(parse *)
  }

  def find(id: Long): Option[Group] = DB.withConnection { implicit c =>
    SQL("""
        SELECT id, name FROM groups WHERE id = {id}
        """).on(
      'id -> id).as(parse.singleOpt)
  }

  def create(name: String) = DB.withConnection { implicit c =>
    SQL("""
        INSERT INTO groups (name) VALUES ({name})
        """).on(
      'name -> name).executeUpdate()
  }

  def update(id: Int, name: String) = DB.withConnection { implicit c =>
    SQL("""
        UPDATE groups SET name = {name} WHERE id = {id}
        """).on(
      'id -> id, 'name -> name).executeUpdate()
  }

  def delete(id: Long) = DB.withConnection { implicit c =>
    SQL("""
        DELETE FROM groups WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()
  }

}
