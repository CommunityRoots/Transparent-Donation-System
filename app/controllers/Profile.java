package controllers;

import Services.EmailService;
import Services.FormValidator;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.Donation;
import models.Need;
import models.User;
import models.Updates;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.need.editNeed;
import views.html.profile.profile;
import views.html.profile.settings;
import views.html.profile.volunteers;

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
            else if(User.findByEmail(email) != null){
                return "Email already in use";
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
        public String category;
        public String message;

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
            else if(admin.role >2){
                return "You do not have permission to do this";
            }
            else if(user.role<3){
                return "User is an admin and cannot be made a volunteer";
            }
            return null;
        }
    }

    public static class EditNeed {
        public String title;
        public String description;
        public int urgency;
        public String location;
        public double amount;
        public String message;

        public String validate(){
            if(title.length()>20){
                return "Title too long. Should be under 60 letters";
            }
            else if(amount <0){
                return "Amount can not be negative number";
            }
            return null;
        }

    }

    public static Result profile(int page) {

        String email = session().get("email");
        User user = User.findByEmail(email);
        int role = user.role;
        PagingList<Need> pagingList;
        //admin or volunteer. Give them needs they added
        if(role<4){
            pagingList = Need.find.where().eq("added_by_id",user.id).findPagingList(3);
        }
        else {
            //Give users needs they donated to
            List<Donation> donationsByUserToNeed = Donation.find.where()
                    .eq("donator_id",user.id)
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
            User user = User.findByEmail(session().get("email"));
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
            User user = User.findByEmail(session().get("email"));
            user.changeEmail(changeEmailForm.get().email);
            session().clear();
            session("email", changeEmailForm.get().email);
            flash("success", "Email Changed");
            return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class)));
        }
    }

    public static Result addNeed(){
        String email = session().get("email");
        User user = User.findByEmail(email);
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
                    user.charity,
                    Need.Category.valueOf(addNeedForm.get().category)
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
        User user = User.findByEmail(email);
        //can delete if they added the need
        //if they are an admin of the charity a need was added into
        if(need != null
                && (user.email.equals(need.addedBy.email) || (user.role==2&& user.charity.equals(need.charity)))
                && user.role<4)
        {
            if(need.deleteNeed()){
                flash("success", "Need Deleted");
            }
            else{
                flash("error","Need already has money donated to it so was not deleted");
            }
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
            User user = User.findByEmail(addVolunteerForm.get().email);
            String email = session().get("email");
            User admin = User.find.byId(email);
            user.changeRole(3);
            user.setCharity(admin.charity);
            return ok();
        }
    }

    public static Result listVolunteers(int page){
        String email = session().get("email");
        User user = User.findByEmail(email);
        if(user.role!=2){
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
        User user = User.findByEmail(email);
        User volunteer = User.findByEmail(id);
        if(user.role > 2 ||
                !user.charity.equals(volunteer.charity)){
            flash("error", "You do not have permission to remove a volunteer");
            return redirect(routes.Profile.listVolunteers(1));
        }
        volunteer.changeRole(4);
        flash("success", "volunteer has been removed");
        return redirect(routes.Profile.listVolunteers(1));
    }

    public static Result editNeed(long id){
        Need need = Need.find.byId(id);
        if(need == null){
            return redirect(
                    routes.Needs.invalidNeed()
            );
        }
        String email = session().get("email");
        User user = User.findByEmail(email);
        if(user.equals(need.addedBy)
                || ((user.role<=2&&user.charity.equals(need.charity)))){
            Form<EditNeed> editNeedForm = form(EditNeed.class);
            EditNeed editNeeds = new EditNeed();
            editNeeds.title = need.title;
            editNeeds.description = need.description;
            editNeeds.location = need.location;
            editNeeds.urgency = need.urgency;
            editNeeds.amount = need.askAmount;
            editNeedForm = editNeedForm.fill(editNeeds);
            return ok(editNeed.render(editNeedForm,id));

        }
        else{
            flash("error", "You do not have permission to edit this need");
            return redirect(routes.Profile.profile(1));
        }

    }

    public static Result doEditNeed(long id){
        Form<EditNeed> editNeedForm = form(EditNeed.class).bindFromRequest();
        Need need = Need.find.byId(id);
        if(editNeedForm.get().message.length()>1) {
            Updates update = new Updates(editNeedForm.get().message, need);
        }
        need.editNeed(editNeedForm.get().title,editNeedForm.get().description,
                editNeedForm.get().location,editNeedForm.get().amount,
                editNeedForm.get().urgency);
        flash("success", "Need has been updated");
        return redirect(routes.Profile.profile(1));
    }
}