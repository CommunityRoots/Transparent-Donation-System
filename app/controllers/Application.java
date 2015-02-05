package controllers;

import models.User;
import play.*;
import play.api.libs.ws.ssl.SystemConfiguration;
import play.data.Form;
import play.mvc.*;
import play.Logger;

import views.html.*;

import java.util.List;

import static play.data.Form.form;

public class Application extends Controller {

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }

    }

    public static Result index() {
        List<User> users = User.find.all();
        Logger.info(""+users.get(0).email);
        return ok(index.render("Your new application is ready."));
    }

    @Security.Authenticated(Secured.class)
    public static Result profile() {
        return ok(profile.render());
    }

    public static Result login(){
        return ok(login.render(form(Login.class)));
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                    routes.Application.profile()
            );
        }
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
                routes.Application.login()
        );
    }

}
