package test.controllers

import java.util.Date

import org.mybatis.scala.session._
import org.specs2.mutable._

import _root_.models._
import play.api.Play.current
import play.api.db._
import play.api.test._
import play.api.test.Helpers._

class GroupControllerSpec extends Specification {

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

  "GroupController" should {

    "グループ一覧" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/groups")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("グループ一覧")
      }
    }

    "グループ登録" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/groups/fresh")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("グループ登録")
      }
    }

    "グループ詳細" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/groups/1")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("グループ詳細")
      }
    }

    "グループ編集" in {
      running(FakeApplication()) {
        MyBatis.dataSource = DB.getDataSource()
        MyBatis.sessionManager.transaction { implicit c => createData(groups, members) }
        val page = route(FakeRequest(GET, "/groups/1/edit")).get
        status(page) must equalTo(OK)
        contentType(page) must beSome.which(_ == "text/html")
        contentAsString(page) must contain("グループ編集")
      }
    }
  }

}
