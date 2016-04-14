package controllers

import play.api.mvc._
import play.api._
/**
 *  15-4-13.
 */
object AccountController extends Controller{
  def login=Action{request=>
    //Ok(views.html.main("")).withCookies(Cookie("name","daxia"),Cookie("token","toooooken"))
    Ok("login")
  }
  def test=Action{
    //Ok(views.html.dd.test("hello test"))
    Ok("test")
  }
}
