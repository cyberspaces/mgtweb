package controllers

import play.api.mvc._
import play.api._
/**
 * Created by yangguo on 15-4-13.
 */
object AccountController extends Controller{
  def login=Action{request=>
    Ok(views.html.main("")).withCookies(Cookie("name","daxia"),Cookie("token","toooooken"))
  }
  def test=Action{
    Ok(views.html.dd.test("hello test"))
  }
}
