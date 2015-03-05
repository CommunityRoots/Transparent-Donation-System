package controllers;

import models.*;
import play.Routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.api.mvc.Cookie;
import play.api.mvc.DiscardingCookie;
import play.mvc.Security;
import scala.collection.Seq;
import views.html.login.*;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;

import java.util.Date;
import java.util.List;

import static play.data.Form.form;

public class Login extends Controller {

    public static class LoginForm {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    public static Result login(){
        return ok(login.render(form(LoginForm.class)));
    }

    public static Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            String email = loginForm.get().email;
            session("email", email);
            User user = User.findByEmail(email);
            user.setLastLogin();
            return redirect(
                    routes.Profile.profile(1)
            );
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Login.login()
        );
    }
}
