package test.models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models.Group
import models.Member
import java.util.Date

class MemberSpec extends Specification {

  val groups = List("グループ００", "グループ０１", "グループ０２")
  val members = List(("メンバー００", new Date), ("メンバー０１", new Date), ("メンバー０２", new Date))

  def createMembers(groups: List[String], members: List[(String, Date)]) =
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
        // 事前条件
        // 実行
        val result = Member.list()
        // 検証
        result must equalTo(List())
      }
    }

    "データが3件ならば、3要素のリストが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val result = Member.list()
        // 検証
        val resultItems = result map { _.name } sortWith { _.compareTo(_) < 0 }
        resultItems must equalTo(members map { _._1 })
      }
    }
  }

  "Member#find" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        // 実行
        val result = Member.find(1)
        // 検証
        result must equalTo(None)
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val result = Member.find(1)
        // 検証
        result.isDefined must beTrue
        result.get.name must equalTo(members(0)._1)
        result.get.birthday.isDefined must beTrue
        result.get.group.isDefined must beTrue
        result.get.group.get.name must equalTo(groups(0))
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val result = Member.find(0)
        // 検証
        result must equalTo(None)
      }
    }
  }

  "Member#update" should {

    "存在するキーを指定したら、データが更新される" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val pre = Member.find(1)
        val result = Member.update(1, "メンバー１０", None, 2)
        val post = Member.find(1)
        // 検証
        result must equalTo(1)
        pre.get.name must equalTo("メンバー００")
        pre.get.birthday.isDefined must beTrue
        pre.get.group.get.name must equalTo("グループ００")
        post.get.name must equalTo("メンバー１０")
        post.get.birthday.isDefined must beFalse
        post.get.group.get.name must equalTo("グループ０１")
      }
    }

    "存在しないキーを指定したら、データが更新されない" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val result = Member.update(0, "メンバー１０", None, 1)
        // 検証
        result must equalTo(0)
      }
    }
  }

  "Member#delete" should {

    "存在するキーを指定したら、データが削除される" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val pre = Member.find(1)
        val result = Member.delete(1)
        val post = Member.find(1)
        // 検証
        result must equalTo(1)
        pre.isDefined must beTrue
        post.isDefined must beFalse
      }
    }

    "存在しないキーを指定したら、データが削除されない" in {
      running(FakeApplication()) {
        // 事前条件
        createMembers(groups, members)
        // 実行
        val result = Member.delete(0)
        // 検証
        result must equalTo(0)
      }
    }
  }

}
