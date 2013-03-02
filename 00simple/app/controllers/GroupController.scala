package controllers

import models._
import views._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

object GroupController extends Controller {

  val groupForm: Form[Group] = Form(mapping(
    "id" -> ignored(0),
    "name" -> nonEmptyText(4, 255),
    "members" -> ignored(List[Member]()))(Group.apply)(Group.unapply))

  def list() = Action {
    Ok(html.groups.list(Group.list()))
  }

  def fresh() = Action {
    Ok(html.groups.fresh(groupForm))
  }

  def create() = Action { implicit req =>
    groupForm.bindFromRequest().fold(
      errors => BadRequest(html.groups.fresh(errors)),
      group => {
        Group.create(group.name)
        Redirect(routes.GroupController.list())
      })
  }

  def show(id: Int) = Action {
    Group.find(id) match {
      case Some(group) => Ok(html.groups.show(group))
      case None => NotFound
    }
  }

  def edit(id: Int) = Action {
    Group.find(id) match {
      case Some(group) => Ok(html.groups.edit(id, groupForm.fill(group)))
      case None => NotFound
    }
  }

  def update(id: Int) = Action { implicit req =>
    groupForm.bindFromRequest().fold(
      errors => BadRequest(html.groups.edit(id, errors)),
      group => {
        Group.update(id, group.name) match {
          case 0 => NotFound
          case _ => Redirect(routes.GroupController.show(id))
        }
      })
  }

  def delete(id: Int) = Action {
    Group.delete(id) match {
      case 0 => NotFound
      case _ => Redirect(routes.GroupController.list())
    }
  }

}
