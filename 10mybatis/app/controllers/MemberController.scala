package controllers

import models._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.db._
import play.api.mvc._
import views.html

object MemberController extends Controller {

  val memberForm: Form[Member] = Form(mapping(
    "id" -> ignored(0),
    "name" -> nonEmptyText(4, 255),
    "birthday" -> optional(date("yyyyMMdd")),
    "groupId" -> number)(Member.apply)(Member.unapply))

  def list() = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Ok(html.members.list(Member.list()))
    }
  }

  def fresh() = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Ok(html.members.fresh(memberForm, Group.list()))
    }
  }

  def create() = Action { implicit req =>
    MyBatis.sessionManager.transaction { implicit s =>
      memberForm.bindFromRequest().fold(
        errors => BadRequest(html.members.fresh(errors, Group.list())),
        member => {
          Group.find(member.groupId) match {
            case Some(_) =>
              Member.create(member)
              Redirect(routes.MemberController.list())
            case None =>
              BadRequest(html.members.fresh(memberForm.fill(member), Group.list()))
          }
        })
    }
  }

  def show(id: Int) = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Member.findWithGroup(id) match {
        case Some(member) => Ok(html.members.show(member))
        case None => NotFound
      }
    }
  }

  def edit(id: Int) = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Member.find(id) match {
        case Some(member) => Ok(html.members.edit(id, memberForm.fill(member), Group.list()))
        case None => NotFound
      }
    }
  }

  def update(id: Int) = Action { implicit req =>
    MyBatis.sessionManager.transaction { implicit s =>
      memberForm.bindFromRequest().fold(
        errors => BadRequest(html.members.edit(id, errors, Group.list())),
        member => {
          Group.find(member.groupId) match {
            case Some(_) =>
              Member.update((id, member)) match {
                case 0 => NotFound
                case _ => Redirect(routes.MemberController.show(id))
              }
            case None =>
              BadRequest(html.members.edit(id, memberForm.fill(member), Group.list()))
          }
        })
    }
  }

  def delete(id: Int) = Action {
    MyBatis.sessionManager.transaction { implicit s =>
      Member.delete(id) match {
        case 0 => NotFound
        case _ => Redirect(routes.MemberController.list())
      }
    }
  }

}
