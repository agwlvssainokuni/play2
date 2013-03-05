package test.models

import java.sql.Connection
import java.util.Date

import org.specs2.mutable._

import models._
import play.api.Play.current
import play.api.db._
import play.api.test._
import play.api.test.Helpers._

class MemberSpec extends Specification {

  val groups = List("グループ００", "グループ０１", "グループ０２")
  val members = List(("メンバー００", new Date), ("メンバー０１", new Date), ("メンバー０２", new Date))

  def createData(groups: List[String], members: List[(String, Date)])(implicit c: Connection) =
    for (item <- (0 until groups.length).zip(groups).zip(members)) {
      item match {
        case ((i, gname), (mname, bdate)) =>
          Group.create(gname)
          Member.create(mname, Some(bdate), i + 1)
      }
    }

  "Member#list" should {

    "データが存在しないならば、空リストが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          // 実行
          val result = Member.list()
          // 検証
          result must beEmpty
        }
      }
    }

    "データが3件ならば、3要素のリストが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.list()
          // 検証
          val resultItems = result map { _.name } sortWith { _.compareTo(_) < 0 }
          resultItems must equalTo(members map { _._1 })
        }
      }
    }
  }

  "Member#find" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          // 実行
          val result = Member.find(1)
          // 検証
          result must beNone
        }
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.find(1)
          // 検証
          result must beSome.which { _.name == members(0)._1 }
          result must beSome.which { _.birthday.isDefined }
        }
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.find(0)
          // 検証
          result must beNone
        }
      }
    }
  }

  "Member#findWithGroup" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          // 実行
          val result = Member.findWithGroup(1)
          // 検証
          result must beNone
        }
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.findWithGroup(1)
          // 検証
          result must beSome.which { case (m, _) => m.name == members(0)._1 }
          result must beSome.which { case (m, _) => m.birthday.isDefined }
          result must beSome.which { case (_, g) => g must beSome.which { _.name == groups(0) } }
        }
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.findWithGroup(0)
          // 検証
          result must beNone
        }
      }
    }
  }

  "Member#update" should {

    "存在するキーを指定したら、データが更新される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val pre = Member.findWithGroup(1)
          val result = Member.update(1, "メンバー１０", None, 2)
          val post = Member.findWithGroup(1)
          // 検証
          result must equalTo(1)
          pre must beSome.which { case (m, _) => m.name == "メンバー００" }
          pre must beSome.which { case (m, _) => m.birthday.isDefined }
          pre must beSome.which { case (_, g) => g must beSome.which { _.name == "グループ００" } }
          post must beSome.which { case (m, _) => m.name == "メンバー１０" }
          post must beSome.which { case (m, _) => m.birthday.isEmpty }
          post must beSome.which { case (_, g) => g must beSome.which { _.name == "グループ０１" } }
        }
      }
    }

    "存在しないキーを指定したら、データが更新されない" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.update(0, "メンバー１０", None, 1)
          // 検証
          result must equalTo(0)
        }
      }
    }
  }

  "Member#delete" should {

    "存在するキーを指定したら、データが削除される" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val pre = Member.find(1)
          val result = Member.delete(1)
          val post = Member.find(1)
          // 検証
          result must equalTo(1)
          pre must beSome
          post must beNone
        }
      }
    }

    "存在しないキーを指定したら、データが削除されない" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.delete(0)
          // 検証
          result must equalTo(0)
        }
      }
    }
  }

}
