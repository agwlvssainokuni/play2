package test.models

import java.util.Date

import org.mybatis.scala.session._
import org.specs2.mutable._

import models._
import play.api.Play.current
import play.api.db.DB
import play.api.test._
import play.api.test.Helpers._

class GroupSpec extends Specification {

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

  "Group#list" should {

    "データが存在しないならば、空リストが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          // 実行
          val result = Group.list()
          // 検証
          result must beEmpty
        }
      }
    }

    "データが3件ならば、3要素のリストが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.list()
          // 検証
          val resultItems = result map { _.name } sortWith { _.compareTo(_) < 0 }
          resultItems must equalTo(groups)
        }
      }
    }
  }

  "Group#find" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          // 実行
          val result = Group.find(1)
          // 検証
          result must beNone
        }
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.find(1)
          // 検証
          result must beSome.which { _.name == groups(0) }
        }
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.find(0)
          // 検証
          result must beNone
        }
      }
    }
  }

  "Group#findWithMembers" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          // 実行
          val result = Group.findWithMembers(1)
          // 検証
          result must beNone
        }
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.findWithMembers(1)
          // 検証
          result must beSome.which { _.name == groups(0) }
          result must beSome.which { !_.members.isEmpty }
        }
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.findWithMembers(0)
          // 検証
          result must beNone
        }
      }
    }
  }

  "Group#update" should {

    "存在するキーを指定したら、データが更新される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val pre = Group.find(1)
          val result = Group.update(Group(1, "グループ１０"))
          val post = Group.find(1)
          // 検証
          result must equalTo(1)
          pre must beSome.which { _.name == "グループ００" }
          post must beSome.which { _.name == "グループ１０" }
        }
      }
    }

    "存在しないキーを指定したら、データが更新されない" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.update(Group(0, "グループ１０"))
          // 検証
          result must equalTo(0)
        }
      }
    }
  }

  "Group#delete" should {

    "存在するキーを指定したら、データが削除される" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val pre = Group.find(1)
          val result = Group.delete(1)
          val post = Group.find(1)
          // 検証
          result must equalTo(1)
          pre must beSome
          post must beNone
        }
      }
    }

    "存在しないキーを指定したら、データが削除されない" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit s =>
          // 事前条件
          createData(groups, members)
          // 実行
          val result = Group.delete(0)
          // 検証
          result must equalTo(0)
        }
      }
    }
  }

}
