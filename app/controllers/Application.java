package controllers;

import models.User;
import models.Need;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

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
        return ok(index.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result profile() {
        return ok(profile.render(User.find.byId(request().username()),
                Need.findByEmail(session().get("email"))
        ));
    }

    public static Result needs() {
        return ok(needs.render(Need.find.all()));
    }

    public static Result viewNeed(int id) {
        Need need = Need.findById(id);
        if(need == null){
            return redirect(
                    routes.Application.needs()
            );
        }
        return ok(viewNeed.render(Need.find.byId(id)));
    }

    public static Result login(){
        return ok(login.render(form(Login.class)));
    }

    @Security.Authenticated(Secured.class)
    public static Result settings(){
        return ok(settings.render());
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
