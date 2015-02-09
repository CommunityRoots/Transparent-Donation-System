package controllers;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.register;


//import javax.mail.internet.AddressException;
//import javax.mail.internet.InternetAddress;

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
            /*if(!isValidEmailAddress(email)){
                return "Invalid email address";
            }*/
            if (User.find.byId(email) != null) {
                return "Email address already in use";
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
            new User(registerForm.get().email, registerForm.get().firstName,
                    BCrypt.hashpw(registerForm.get().password,BCrypt.gensalt(12))).save();
            session("email", registerForm.get().email);
            return redirect(
                    routes.Application.profile()
            );
        }
    }

    /*public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }*/
}
