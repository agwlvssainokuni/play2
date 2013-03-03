package controllers

import play.api._
import play.api.mvc._
import views.html

object Application extends Controller {

  def index = Action {
    Ok(html.index("Your new application is ready."))
  }

}
