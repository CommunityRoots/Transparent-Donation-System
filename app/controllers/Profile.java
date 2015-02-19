package controllers;

import models.Need;
import models.User;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.profile;
import views.html.settings;

import static play.data.Form.form;
import static play.mvc.Controller.flash;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;


public class Profile {

    public static class ChangePass{
        public String oldPassword;
        public String newPassword;
        public String retypePassword;

        public String validate() {
            if(retypePassword!=newPassword){
                return "Passwords did not match";
            }

            return null;
        }
    }

    public static class ChangeEmail {
        public String email;
        public String password;

        public String validate(){
            return null;
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result profile() {
        return ok(profile.render(User.find.byId(request().username()),
                Need.findByEmail(session().get("email"))
        ));
    }

    @Security.Authenticated(Secured.class)
    public static Result settings(){
        return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class)));
    }

    public static Result changePassword(){
        Form<ChangePass> changePassForm = form(ChangePass.class).bindFromRequest();
        if (changePassForm.hasErrors()) {
            return badRequest(settings.render(changePassForm,form(ChangeEmail.class)));
        } else {
            User user = User.find.byId(session().get("email"));
            user.changePassword(changePassForm.get().newPassword);
            flash("success", "Password Changed");
            return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class)));
        }
    }

    public static Result changeEmail(){
        Form<ChangeEmail> changeEmailForm = form(ChangeEmail.class).bindFromRequest();
        if (changeEmailForm.hasErrors()) {
            return badRequest(settings.render(form(ChangePass.class),changeEmailForm));
        } else {
            User user = User.find.byId(session().get("email"));
            user.changeEmail(changeEmailForm.get().email);
            flash("success", "Password Changed");
            return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class)));
        }
    }
}
