package controllers

import play.api._
import play.api.db.slick.dbSessionRequestAsSession
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.concurrent.ExecutionContext.Implicits.global
import models._
import play.api.mvc.Flash
import org.slf4j.LoggerFactory
import play.api.db.slick.DBAction
import sun.security.util.Password
import java.util.Date

object Application extends Controller {
  val userForm: Form[Knol] = Form(
    mapping(
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "company" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "mobile" -> longNumber,
      "userType" -> ignored("User"),
      "created" -> ignored(new Date),
      "updated" -> ignored(new Date),
      "id" -> ignored(0))(Knol.apply)(Knol.unapply))

  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText) verifying ("Invalid email or password", result => result match {
        case (email, password) => MyModel.check(email, password).isDefined
      }))

  /**
   * This method redirects the user to the Home Page
   * @return : Nothing
   */
  def index = Action { implicit request =>
    Ok(views.html.index(""))
  }

  /**
   * This method redirects the user to the SignUp page
   * @return : Nothing
   */
  def signup = Action { implicit request =>
    Ok(views.html.signupForm(userForm))
  }

  /**
   * This method method is invoked on the signUp 
   * If form contains the validation errors, form with errors is returned
   * @return : Nothing
   */
  def userFormSubmit = DBAction { implicit request =>
    val newUserForm = userForm.bindFromRequest()
    newUserForm.fold(
      formWithErrors => {
        BadRequest(views.html.index(""))
      },
      success = { newUser =>
        MyModel.insertDetail(newUser)
        Redirect(routes.Application.index()).flashing("success" -> "Member Added Successfully")
      })
  }

  /**
   * This method redirects the user to the login screen
   * @return : Nothing
   */
  def login = Action { implicit request =>
    Ok(views.html.loginForm(loginForm))
  }

  /**This method authenticates the user
   * @return : Nothing
   */
  def authenticate = DBAction { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.loginForm(formWithErrors)),
      success = { newUser =>
        val knol = MyModel.findUser(newUser._1)
        Ok(views.html.userProfile(knol.get)).withSession("email" -> knol.get.email).flashing("success" -> "Welcome")
      })
  }

  /**
   * This method redirects the user to the update form
   * @param id
   * @return
   */
  def editForm(id: Int) = DBAction { implicit request =>
    MyModel.findById(id).map {
      user => Ok(views.html.updateForm(id, userForm.fill(user)))
    }.getOrElse(NotFound)
  }

  /**This method calls the model's method to perform updations if the form is filled correctly,
   * otherwise form with errors is returned
   * @param id
   * @return
   */
  def editDetail(id: Int) = DBAction { implicit request =>
    val newUserForm = userForm.bindFromRequest()
    newUserForm.fold(
      hasErrors = { form =>
        Ok(views.html.updateForm(id, form)).flashing("error" -> "Error in form")
      },
      success = { newUser =>
        MyModel.editDetail(id, newUser)
        Ok(views.html.userProfile(newUser)).flashing("success" -> "Member Updated Successfully")
      })
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.index).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

}