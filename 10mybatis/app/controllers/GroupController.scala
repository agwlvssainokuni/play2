package controllers

import models._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.db._
import play.api.mvc._
import views.html

object GroupController extends Controller {

  val groupForm: Form[Group] = Form(mapping(
    "id" -> ignored(0),
    "name" -> nonEmptyText(4, 255))(Group.apply)(Group.unapply))

  def list() = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Ok(html.groups.list(Group.list()))
    }
  }

  def fresh() = Action {
    Ok(html.groups.fresh(groupForm))
  }

  def create() = Action { implicit req =>
    MyBatis.sessionManager.transaction { implicit s =>
      groupForm.bindFromRequest().fold(
        errors => BadRequest(html.groups.fresh(errors)),
        group => {
          Group.create(group)
          Redirect(routes.GroupController.list())
        })
    }
  }

  def show(id: Int) = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Group.findWithMembers(id) match {
        case Some(group) => Ok(html.groups.show(group))
        case None => NotFound
      }
    }
  }

  def edit(id: Int) = Action {
    MyBatis.sessionManager.managed { implicit s =>
      Group.find(id) match {
        case Some(group) => Ok(html.groups.edit(id, groupForm.fill(group)))
        case None => NotFound
      }
    }
  }

  def update(id: Int) = Action { implicit req =>
    MyBatis.sessionManager.transaction { implicit s =>
      groupForm.bindFromRequest().fold(
        errors => BadRequest(html.groups.edit(id, errors)),
        group => {
          Group.update((id, group)) match {
            case 0 => NotFound
            case _ => Redirect(routes.GroupController.show(id))
          }
        })
    }
  }

  def delete(id: Int) = Action {
    MyBatis.sessionManager.transaction { implicit s =>
      Group.delete(id) match {
        case 0 => NotFound
        case _ => Redirect(routes.GroupController.list())
      }
    }
  }

}
