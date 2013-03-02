package test.models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import models.Group

class GroupSpec extends Specification {

  val groups = List("グループ００", "グループ０１", "グループ０２")

  def createGroups(groups: List[String]) =
    for (n <- groups) {
      Group.create(n)
    }

  "Group#list" should {

    "データが存在しないならば、空リストが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        // 実行
        val result = Group.list()
        // 検証
        result must equalTo(List())
      }
    }

    "データが3件ならば、3要素のリストが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val result = Group.list()
        // 検証
        val resultItems = result map { _.name } sortWith { _.compareTo(_) < 0 }
        resultItems must equalTo(groups)
      }
    }
  }

  "Group#find" should {

    "データが存在しないならば、Noneが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        // 実行
        val result = Group.find(1)
        // 検証
        result must equalTo(None)
      }
    }

    "データが3件あって、存在するキーを指定したら、データが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val result = Group.find(1)
        // 検証
        result.isDefined must beTrue
        result.get.name must equalTo(groups(0))
      }
    }

    "データが3件あって、存在しないキーを指定したら、Noneが返却される" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val result = Group.find(0)
        // 検証
        result must equalTo(None)
      }
    }
  }

  "Group#update" should {

    "存在するキーを指定したら、データが更新される" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val pre = Group.find(1)
        val result = Group.update(1, "グループ１０")
        val post = Group.find(1)
        // 検証
        result must equalTo(1)
        pre.get.name must equalTo("グループ００")
        post.get.name must equalTo("グループ１０")
      }
    }

    "存在しないキーを指定したら、データが更新されない" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val result = Group.update(0, "グループ１０")
        // 検証
        result must equalTo(0)
      }
    }
  }

  "Group#delete" should {

    "存在するキーを指定したら、データが削除される" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val pre = Group.find(1)
        val result = Group.delete(1)
        val post = Group.find(1)
        // 検証
        result must equalTo(1)
        pre.isDefined must beTrue
        post.isDefined must beFalse
      }
    }

    "存在しないキーを指定したら、データが削除されない" in {
      running(FakeApplication()) {
        // 事前条件
        createGroups(groups)
        // 実行
        val result = Group.delete(0)
        // 検証
        result must equalTo(0)
      }
    }
  }

}
