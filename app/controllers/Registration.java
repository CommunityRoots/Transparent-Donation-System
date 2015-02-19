package controllers;
import models.EmailService;
import models.PasswordValidator;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.register;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static play.data.Form.form;

public class Registration extends Controller {

    public static class RegisterForm {

        public String firstName;
        public String lastName;
        public String email;
        public String password;

        public String validate() {
            PasswordValidator passwordValidator = new PasswordValidator();
            if(firstName == null || firstName.equals("") || lastName == null || lastName.equals("") ||
            email == null || email.equals("") || email == null || email.equals("")){
                return "You must enter every field";
            }
            if(!isValidEmailAddress(email)){
                return "Invalid email address";
            }
            if (User.find.byId(email) != null) {
                return "Email address already in use";
            }
            if(!passwordValidator.validate(password)){
                return  "Password must have 1 number, between 6-20 characters";
            }
            return null;
        }
    }

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
            session("email", email);
            EmailService emailService = new EmailService();
            emailService.sendEmail(firstName,email,"Welcome to Community Roots",
                    "Welcome to CommunityRoots. Please feel free to donate money towards needs.");
            return redirect(
                    routes.Profile.profile()
            );
        }
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
