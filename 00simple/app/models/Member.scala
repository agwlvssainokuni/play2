package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._
import java.util.Date

case class Member(id: Int, name: String, birthday: Option[Date], group: Option[Group])

object Member {

  private val parse = {
    get[Int]("id") ~ get[String]("name") ~ get[Option[Date]]("birthday") map {
      case id ~ name ~ birthday => Member(id, name, birthday, None)
    }
  }

  private val parseGroup = {
    get[Int]("groups.id") ~ get[String]("groups.name") map {
      case groupId ~ groupName => Group(groupId, groupName, List())
    }
  }

  private val parseJoined = {
    get[Int]("members.id") ~ get[String]("members.name") ~ get[Option[Date]]("members.birthday") ~ parseGroup map {
      case id ~ name ~ birthday ~ group => Member(id, name, birthday, Some(group))
    }
  }

  def list(): List[Member] = DB.withConnection { implicit c =>
    SQL("""
        SELECT id, name, birthday FROM members
        """).as(parse *)
  }

  def find(id: Long): Option[Member] = DB.withConnection { implicit c =>
    SQL("""
        SELECT
            A.id,
            A.name,
            A.birthday,
            B.id,
            B.name
        FROM
            members AS A
            JOIN
            groups AS B
            ON
              A.group_id = B.id
        WHERE
            A.id = {id}
        """).on(
      'id -> id).as(parseJoined.singleOpt)
  }

  def create(name: String, birthday: Option[Date], groupId: Int) = DB.withConnection { implicit c =>
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
      'name -> name, 'birthday -> birthday, 'groupId -> groupId).executeUpdate()
  }

  def update(id: Int, name: String, birthday: Option[Date], groupId: Int) = DB.withConnection { implicit c =>
    SQL("""
        UPDATE members
        SET
            name = {name},
            birthday = {birthday},
            group_id = {groupId}
        WHERE
            id = {id}
        """).on(
      'id -> id, 'name -> name, 'birthday -> birthday, 'groupId -> groupId).executeUpdate()
  }

  def delete(id: Long) = DB.withConnection { implicit c =>
    SQL("""
        DELETE FROM members WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()
  }

}
