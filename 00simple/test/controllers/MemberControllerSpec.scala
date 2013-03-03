package test.controllers

import java.sql.Connection
import java.util.Date

import org.specs2.mutable._

import _root_.models._
import play.api.Play.current
import play.api.db._
import play.api.test._
import play.api.test.Helpers._

class MemberControllerSpec extends Specification {

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

  "MemberController" should {

    "メンバー一覧" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/members")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("メンバー一覧")
      }
    }

    "メンバー登録" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/members/fresh")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("メンバー登録")
      }
    }

    "メンバー詳細" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/members/1")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("メンバー詳細")
      }
    }

    "メンバー編集" in {
      running(FakeApplication()) {
        DB.withTransaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/members/1/edit")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("メンバー編集")
      }
    }
  }

}
