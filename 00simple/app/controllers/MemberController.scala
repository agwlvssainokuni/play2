package controllers

import models._
import views._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

object MemberController extends Controller {

  private val groupMapping = mapping(
    "id" -> number,
    "name" -> ignored(""),
    "members" -> ignored(List[Member]()))(Group.apply)(Group.unapply)

  val memberForm: Form[Member] = Form(mapping(
    "id" -> ignored(0),
    "name" -> nonEmptyText(4, 255),
    "birthday" -> optional(date("yyyyMMdd")),
    "group" -> optional(groupMapping))(Member.apply)(Member.unapply))

  def list() = Action {
    Ok(html.members.list(Member.list()))
  }

  def fresh() = Action {
    Ok(html.members.fresh(memberForm, Group.list()))
  }

  def create() = Action { implicit req =>
    memberForm.bindFromRequest().fold(
      errors => BadRequest(html.members.fresh(errors, Group.list())),
      member => {
        member.group match {
          case Some(group) =>
            Member.create(member.name, member.birthday, group.id)
            Redirect(routes.MemberController.list())
          case None =>
            BadRequest(html.members.fresh(memberForm.fill(member), Group.list()))
        }
      })
  }

  def show(id: Int) = Action {
    Member.find(id) match {
      case Some(member) => Ok(html.members.show(member))
      case None => NotFound
    }
  }

  def edit(id: Int) = Action {
    Member.find(id) match {
      case Some(member) => Ok(html.members.edit(id, memberForm.fill(member), Group.list()))
      case None => NotFound
    }
  }

  def update(id: Int) = Action { implicit req =>
    memberForm.bindFromRequest().fold(
      errors => BadRequest(html.members.edit(id, errors, Group.list())),
      member => {
        member.group match {
          case Some(group) =>
            Member.update(id, member.name, member.birthday, group.id) match {
              case 0 => NotFound
              case _ => Redirect(routes.MemberController.show(id))
            }
          case None =>
            BadRequest(html.members.edit(id, memberForm.fill(member), Group.list()))
        }
      })
  }

  def delete(id: Int) = Action {
    Member.delete(id) match {
      case 0 => NotFound
      case _ => Redirect(routes.MemberController.list())
    }
  }

}
