package controllers;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.*;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.profile;
import views.html.settings;
import views.html.volunteers;

import java.util.LinkedList;
import java.util.List;

import static play.data.Form.form;
import static play.mvc.Controller.flash;
import static play.mvc.Controller.session;
import static play.mvc.Results.*;

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
        public int urgency;
        public String location;

        public String validate(){
            if(title.length() >20 || title.length() <5){
                return "Title length should be greater than 4 and less than 21";
            }
            else if(urgency >10 || urgency <0){
                return "Urgency must be in the range of 1 - 10";
            }
            else if(amount <0){
                return "Amount cannot be a negative number";
            }

            return null;
        }
    }

    public static class AddVolunteer {
        public String email;

        public String validate(){
            String adminEmail = session().get("email");
            User admin = User.find.byId(adminEmail);
            User user = User.find.byId(email);

            if(User.find.byId(email) == null){
                return "No user with that email";
            }
            else if(!admin.role.equals("admin")){
                return "You do not have permission to do this";
            }
            else if(user.role.equals("admin")){
                return "User is an admin and cannot be made a volunteer";
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
            pagingList =  Need.find.where().in("added_by_email",email).findPagingList(3);
        }
        else {
            List<Donation> donationsByUserToNeed = Donation.find.where()
                    .eq("donator_email",email)
                    .findList();
            LinkedList<Long> needids = new LinkedList<>();
            for(Donation donation : donationsByUserToNeed) {
                needids.add(donation.need.id);
            }
            pagingList =  Need.find.where().in("id",needids).findPagingList(3);
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
                    user,
                    addNeedForm.get().amount,
                    addNeedForm.get().location,
                    addNeedForm.get().urgency,
                    user.charity
                    );
            return ok();
        }
    }

    /**
     *
     * @param id identifier for the need
     * @return redirect to profile with status of deletion
     * You can delete a need only if you are an admin or volunteer
     * Admins of a charity can delete needs added by volunteers of that charity
     * Volunteers can delete needs they added
     */
    public static Result deleteNeed(long id) {
        Need need = Need.find.byId(id);
        String email = session().get("email");
        User user = User.find.byId(email);
        if(need != null
                && (user.email.equals(need.addedBy.email) || (user.role.equals("admin")&& user.charity.equals(need.charity)))
                && (user.role.equals("admin")||user.role.equals("volunteer")))
        {
            need.delete();
            flash("success", "Need Deleted");
        }
        else {
            flash("error", "Need Was not deleted. Invalid need or permissions.");
        }

        return redirect(routes.Profile.profile(1));
    }

    public static Result addVolunteer(){
        Form<AddVolunteer> addVolunteerForm = form(AddVolunteer.class).bindFromRequest();
        if(addVolunteerForm.hasErrors()){
            return badRequest(addVolunteerForm.errorsAsJson()).as("application/json");
        }
        else {
            User user = User.find.byId(addVolunteerForm.get().email);
            String email = session().get("email");
            User admin = User.find.byId(email);
            user.changeRole("volunteer");
            user.setCharity(admin.charity);
            return ok();
        }
    }

    public static Result listVolunteers(int page){
        String email = session().get("email");
        User user = User.find.byId(email);
        String role = user.role;
        if(!role.equals("admin")){
            return redirect(routes.Application.index());
        }

        //users that have role volunteer and same charity as user
        PagingList<User> pagingList = User.find.where()
                .eq("role","volunteer")
                .eq("charity",user.charity)
                .findPagingList(10);

        Page<User> currentPage = pagingList.getPage(page - 1);
        List<User> volunteer = currentPage.getList();

        Integer totalPageCount = pagingList.getTotalPageCount();
        return ok(volunteers.render(
                        volunteer, page, totalPageCount)
        );
    }

    public static Result deleteVolunteer(String id){
        String email = session().get("email");
        User user = User.find.byId(email);
        User volunteer = User.find.byId(id);
        if(!user.role.equals("admin") ||
                !user.charity.equals(volunteer.charity)){
            flash("error", "You do not have permission to remove a volunteer");
            return redirect(routes.Profile.listVolunteers(1));
        }
        volunteer.changeRole("user");
        flash("success", "volunteer has been removed");
        return redirect(routes.Profile.listVolunteers(1));
    }

    public static Result editNeed(long id){
        return ok();
    }
}