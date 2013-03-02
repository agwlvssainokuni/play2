package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db._
import java.util.Date

case class Member(id: Int, name: String, birthday: Option[Date], groupId: Int) {
  var group: Option[Group] = None
}

object Member {

  private val parse = {
    get[Int]("members.id") ~ get[String]("members.name") ~ get[Option[Date]]("members.birthday") ~ get[Int]("members.group_id") map {
      case id ~ name ~ birthday ~ groupId => Member(id, name, birthday, groupId)
    }
  }

  private val parseGroup = {
    get[Int]("groups.id") ~ get[String]("groups.name") map {
      case groupId ~ groupName => Group(groupId, groupName)
    }
  }

  private val parseJoined = {
    parse ~ parseGroup map {
      case member ~ group =>
        member.group = Some(group)
        member
    }
  }

  def list(): List[Member] = DB.withConnection { implicit c =>
    SQL("""
        SELECT id, name, birthday, group_id FROM members
        """).as(parse *)
  }

  def find(id: Int): Option[Member] = DB.withConnection { implicit c =>
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

  def delete(id: Int) = DB.withConnection { implicit c =>
    SQL("""
        DELETE FROM members WHERE id = {id}
        """).on(
      'id -> id).executeUpdate()
  }

}
