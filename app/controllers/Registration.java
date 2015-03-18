package controllers;
import Services.EmailService;
import Services.FormValidator;
import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.cache.Cached;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login.register;
import java.net.MalformedURLException;
import play.Logger;

import static play.data.Form.form;

public class Registration extends Controller {

    public static class RegisterForm {

        public String firstName;
        public String lastName;
        public String email;
        public String password;

        public String validate() {
            if(firstName == null || firstName.equals("") || lastName == null || lastName.equals("") ||
            email == null || email.equals("") || email == null || email.equals("")){
                return "You must enter every field";
            }
            if(!FormValidator.isValidEmailAddress(email)){
                return "Invalid email address";
            }
            if (User.findByEmail(email) != null) {
                return "Email address already in use";
            }
            if(!FormValidator.validate(password)){
                return  "Password must have 1 number, between 6-20 characters";
            }
            return null;
        }
    }

    @Cached(key = "register", duration = 10)
    public static Result register(){
        return ok(register.render(form(RegisterForm.class)));
    }

    public static Result authenticate() {
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();
        if (registerForm.hasErrors()) {
            return badRequest(register.render(registerForm));
        } else {
            session().clear();
            String firstName = registerForm.get().firstName;
            String email = registerForm.get().email;
            new User(registerForm.get().email, firstName,
                    registerForm.get().lastName,
                    BCrypt.hashpw(registerForm.get().password,BCrypt.gensalt(12))).save();
            try {
                Token.sendConfirmEmail(User.findByEmail(email));
            } catch (MalformedURLException e) {
                Logger.error("Cannot validate URL", e);
            }
            flash("success", "A confirmation email has been sent to your email account. Please click the link in the email to verify your email account.");
            return redirect(
                    routes.Login.login()
            );
        }
    }

    public static Result confirm(String token) {
        if (token == null) {
            flash("error", "Invalid link");
        } else {
            Token confirmToken = Token.findByToken(token);
            if (confirmToken == null) {
                flash("error", "error.technical");
            } else if (confirmToken.isExpired()) {
                confirmToken.delete();
                flash("error", "Link has expired");
            } else {
                User.findByEmail(confirmToken.email).validate();
            }
        }
        flash("success", "Thanks for verifying your account. You can now sign in below.");
        return redirect(
                routes.Login.login()
        );
    }

    public static Result sendConfirmEmail(String email) {
        try {
            Token.sendConfirmEmail(User.findByEmail(email));
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
        }
        return redirect(routes.Login.login());
    }


}
