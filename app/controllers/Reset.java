package controllers;

import models.FormValidator;
import models.Token;
import models.User;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.forgot.forgot;
import views.html.forgot.reset;


import java.net.MalformedURLException;

import static play.data.Form.form;

public class Reset extends Controller {

    public static class ForgotPass {
        public String email;

        public String validate() {
            if(User.find.byId(email)==null){
                return "No account registered with that email";
            }
            return null;
        }
    }

    public static class NewPass {
        public String password;
        public String reEnteredPassword;

        public String validate() {
            if(!password.equals(reEnteredPassword)){
                return "Passwords do not match";
            }
            else if(!FormValidator.validate(password)){
                return  "Password must have 1 number, between 6-20 characters";
            }
            return null;

        }

    }

    public static Result forgot() { return ok(forgot.render(form(ForgotPass.class)));}

    public static Result doForgot() {
        Form<ForgotPass> askForm = form(ForgotPass.class).bindFromRequest();

        if (askForm.hasErrors()) {
            flash("error", "Invalid email");
            return badRequest(forgot.render(askForm));
        }

        final String email = askForm.get().email;
        User user = User.find.byId(email);
        if (user == null) {
            flash("error", ("No such user"));
            return badRequest(forgot.render(askForm));
        }

        try {
            Token.sendmail(user);
            flash("success", "Reset email has been sent");
            return ok(forgot.render(form(ForgotPass.class)));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(forgot.render(askForm));
    }

    public static Result reset(String token) {

        if (token == null) {
            flash("error", "Invalid link");
            Form<ForgotPass> askForm = form(ForgotPass.class);
            return badRequest(forgot.render(askForm));
        }

        Token resetToken = Token.findByTokenAndType(token);
        if (resetToken == null) {
            flash("error", "error.technical");
            Form<ForgotPass> askForm = form(ForgotPass.class);
            return badRequest(forgot.render(askForm));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("error", "Link has expired");
            Form<ForgotPass> askForm = form(ForgotPass.class);
            return badRequest(forgot.render(askForm));
        }

        Form<NewPass> resetForm = form(NewPass.class);
        return ok(reset.render(resetForm, token));
    }

    public static Result doNewPass(String token){
        Form<NewPass> resetForm = form(NewPass.class).bindFromRequest();
        if (resetForm.hasErrors()) {
            return badRequest(reset.render(resetForm,token));
        }
        try {
            Token resetToken = Token.findByTokenAndType(token);
            if (resetToken == null) {
                flash("error", "error.technical");
                Form<ForgotPass> askForm = form(ForgotPass.class);
                return badRequest(reset.render(resetForm,token));
            }

            if (resetToken.isExpired()) {
                resetToken.delete();
                flash("error", "Link has expired");
                Form<ForgotPass> askForm = form(ForgotPass.class);
                return badRequest(reset.render(resetForm,token));
            }
            User user = User.find.byId(resetToken.email);
            if (user == null) {
                flash("error", ("No such user"));
                return badRequest(reset.render(resetForm,token));
            }
            String password = resetForm.get().password;
            user.changePassword(password);
            resetToken.delete();
            flash("success", "Password changed");
            return ok(reset.render(resetForm, token));
        } catch (Exception e){
            flash("error", "Reset link invalid");
            return badRequest(reset.render(resetForm, token));
        }

    }

}
