package test.models

import java.util.Date

import org.mybatis.scala.session._
import org.specs2.mutable._

import models._
import play.api.Play
import play.api.test._
import play.api.test.Helpers._

class MemberSpec extends Specification {

  val groups = List("グループ００", "グループ０１", "グループ０２")
  val members = List(("メンバー００", new Date), ("メンバー０１", new Date), ("メンバー０２", new Date))

  def createData(groups: List[String], members: List[(String, Date)])(implicit s: Session) =
    for (item <- (0 until groups.length).zip(groups).zip(members)) {
      item match {
        case ((i, gname), (mname, bdate)) =>
          Group.create(Group(-1, gname))
          Member.create(Member(-1, mname, Some(bdate), i + 1))
      }
    }

  "Member#list" should {

    "データが存在しないならば、空リストが返却される" in {
      running(FakeApplication()) {
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.findWithGroup(1)
          // 検証
          result must beSome.which { _.name == members(0)._1 }
          result must beSome.which { _.birthday.isDefined }
          result must beSome.which { _.group.name == groups(0) }
        }
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val pre = Member.findWithGroup(1)
          val result = Member.update(Member(1, "メンバー１０", None, 2))
          val post = Member.findWithGroup(1)
          // 検証
          result must equalTo(1)
          pre must beSome.which { _.name == "メンバー００" }
          pre must beSome.which { _.birthday.isDefined }
          pre must beSome.which { _.group.name == "グループ００" }
          post must beSome.which { _.name == "メンバー１０" }
          post must beSome.which { _.birthday.isEmpty }
          post must beSome.which { _.group.name == "グループ０１" }
        }
      }
    }

    "存在しないキーを指定したら、データが更新されない" in {
      running(FakeApplication()) {
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Member.update(Member(0, "メンバー１０", None, 1))
          // 検証
          result must equalTo(0)
        }
      }
    }
  }

  "Member#delete" should {

    "存在するキーを指定したら、データが削除される" in {
      running(FakeApplication()) {
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
        MyBatis.application = Play.current
        MyBatis.withTransaction { implicit s =>
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
