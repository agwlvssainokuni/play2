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
    DB.withConnection { implicit c =>
      Ok(html.groups.list(Group.list()))
    }
  }

  def fresh() = Action {
    Ok(html.groups.fresh(groupForm))
  }

  def create() = Action { implicit req =>
    DB.withTransaction { implicit c =>
      groupForm.bindFromRequest().fold(
        errors => BadRequest(html.groups.fresh(errors)),
        group => {
          Group.create(group.name)
          Redirect(routes.GroupController.list())
        })
    }
  }

  def show(id: Int) = Action {
    DB.withConnection { implicit c =>
      Group.find(id) match {
        case Some(group) => Ok(html.groups.show(group))
        case None => NotFound
      }
    }
  }

  def edit(id: Int) = Action {
    DB.withConnection { implicit c =>
      Group.find(id) match {
        case Some(group) => Ok(html.groups.edit(id, groupForm.fill(group)))
        case None => NotFound
      }
    }
  }

  def update(id: Int) = Action { implicit req =>
    DB.withTransaction { implicit c =>
      groupForm.bindFromRequest().fold(
        errors => BadRequest(html.groups.edit(id, errors)),
        group => {
          Group.update(id, group.name) match {
            case 0 => NotFound
            case _ => Redirect(routes.GroupController.show(id))
          }
        })
    }
  }

  def delete(id: Int) = Action {
    DB.withTransaction { implicit c =>
      Group.delete(id) match {
        case 0 => NotFound
        case _ => Redirect(routes.GroupController.list())
      }
    }
  }

}
