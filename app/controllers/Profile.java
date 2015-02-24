package controllers;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.*;
import play.cache.Cache;
import play.cache.Cached;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.profile;
import views.html.settings;

import java.util.LinkedList;
import java.util.List;

import static play.data.Form.form;
import static play.mvc.Controller.flash;
import static play.mvc.Controller.request;
import static play.mvc.Controller.session;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.forbidden;
import static play.mvc.Results.ok;

@Security.Authenticated(Secured.class)
public class Profile {

    public static class ChangePass{
        public String oldPassword;
        public String newPassword;
        public String retypePassword;

        public String validate() {
            if(!FormValidator.validate(newPassword)){
                return  "Password must have 1 number, between 6-20 characters";
            }
            else if(!retypePassword.equals(newPassword)){
                return "Passwords did not match";
            }
            else if (User.authenticate(session().get("email"), oldPassword) == null) {
                return "Invalid password";
            }
            return null;
        }
    }

    public static class ChangeEmail {
        public String email;
        public String password;

        public String validate(){
            if (User.authenticate(session().get("email"), password) == null) {
                return "Invalid password";
            }
            else if(!FormValidator.validate(email)){
                return "Email invalid";
            }
            return null;
        }
    }

    public static class AddNeed {
        public String title;
        public double amount;
        public String description;

        public String validate(){
            if(title.length() >20 || title.length() <5){
                return "Title length should be greater than 4 and less than 21";
            }

            return null;
        }
    }

    public static class AddVolunteer {
        public String email;

        public String validate(){
            if(User.find.byId(email) == null){
                return "No user with that email";
            }
            return null;
        }
    }



    public static Result profile(int page) {

        String email = session().get("email");
        User user = User.find.byId(email);
        String role = user.role;
        PagingList<Need> pagingList;
        if(role.equals("admin")||role.equals("volunteer")){
            pagingList =  Need.find.where().in("added_by",email).findPagingList(3);
        }
        else {
            List<Donation> donationsByUserToNeed = Donation.find.where()
                    .eq("donator_email",email)
                    .findList();
            LinkedList<Long> needids = new LinkedList<>();
            for(Donation donation : donationsByUserToNeed) {
                needids.add(donation.needId);
            }
            pagingList =  Need.find.where().in("id",needids).findPagingList(5);
        }

        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needs = currentPage.getList();

        Integer totalPageCount = pagingList.getTotalPageCount();

        return ok(profile.render(form(AddNeed.class),user,
                needs, page, totalPageCount)
        );
    }

    public static Result settings(){
        return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class)));
    }

    public static Result changePassword(){
        Form<ChangePass> changePassForm = form(ChangePass.class).bindFromRequest();
        if (changePassForm.hasErrors()) {
            return forbidden();
        } else {
            User user = User.find.byId(session().get("email"));
            user.changePassword(changePassForm.get().newPassword);
            EmailService emailService = new EmailService();
            emailService.sendEmail(user.firstName,user.email,"Password Change","Your Password has been changed.");
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

    public static Result addNeed(){
        String email = session().get("email");
        User user = User.find.byId(email);
        Form<AddNeed> addNeedForm = form(AddNeed.class).bindFromRequest();
        if(addNeedForm.hasErrors()){
            return badRequest(addNeedForm.errorsAsJson()).as("application/json");
        }
        else {
            Need need = new Need();
            need.addNeed(addNeedForm.get().title,
                    addNeedForm.get().description,
                    session().get("email"),
                    addNeedForm.get().amount
                    );
            return ok();
        }
    }

    public static Result deleteNeed(long id) {
        Need need = Need.findById(id);
        if(need != null){
            need.delete();
            return profile(1);
        }
        else {
            return badRequest();
        }
    }

    public static Result addVolunteer(){
        Form<AddVolunteer> addVolunteerForm = form(AddVolunteer.class).bindFromRequest();
        if(addVolunteerForm.hasErrors()){
            return badRequest(addVolunteerForm.errorsAsJson()).as("application/json");
        }
        else {
            User user = User.find.byId(addVolunteerForm.get().email);
            user.changerole("volunteer");
            return ok();
        }

    }
}
