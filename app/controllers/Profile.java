package controllers;

import Services.EmailService;
import Services.FormValidator;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import models.*;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import views.html.need.editNeed;
import views.html.profile.profile;
import views.html.profile.settings;
import views.html.profile.subscriptions;
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
            User admin = User.findByEmail(adminEmail);
            User user = User.findByEmail(email);

            if(User.findByEmail(email) == null){
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
        public String category;
        public String message;
        public String reason;
        public boolean closed;

        public String validate(){
            String email = session().get("email");
            User user = User.findByEmail(email);
            if(title.length()>20){
                return "Title too long. Should be under 60 letters";
            }
            else if(amount <0){
                return "Amount can not be negative number";
            }
            else if(user.role>2){
                return "You do not have permissions to do this";
            }
            else if(closed){
                if(reason==null ||reason.equals("")){
                    return "To close a need you must add a reason";
                }
            }
            return null;
        }
    }
    public static class EditCharity {
        public String charityName;
        public String website;
        public String description;

        public String validate(){
            if(charityName.length()==0 || charityName == null){
                return "Please enter charity name";
            }
            if(website.contains("http")){
                return "Website should in form of www.charityAddress.com";
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
        if(role==3){
            pagingList = Need.find.where().eq("added_by_id",user.id).findPagingList(6);
        }
        else if(role<3){
            pagingList = Need.find.where().eq("charity_id", user.charity.id).findPagingList(6);
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
            pagingList =  Need.find.where().in("id",needids).findPagingList(6);
        }

        Page<Need> currentPage = pagingList.getPage(page - 1);
        List<Need> needs = currentPage.getList();

        Integer totalPageCount = pagingList.getTotalPageCount();
        return ok(profile.render(form(AddNeed.class),preFillEditNeedForm(user),user,
                needs, page, totalPageCount)
        );
    }

    public static Result settings(){
        String email = session().get("email");
        User user = User.findByEmail(email);
        return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class),user));
    }

    public static Result changePassword(){
        User user = User.findByEmail(session().get("email"));
        Form<ChangePass> changePassForm = form(ChangePass.class).bindFromRequest();
        if (changePassForm.hasErrors()) {
            return badRequest(settings.render(changePassForm, form(ChangeEmail.class), user));
        } else {
            user.changePassword(changePassForm.get().newPassword);
            EmailService emailService = new EmailService();
            emailService.sendEmail(user.firstName,user.email,"Password Change","Your Password has been changed.");
            flash("success", "Password Changed");
            return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class),user));
        }
    }

    public static Result changeEmail(){
        User user = User.findByEmail(session().get("email"));
        Form<ChangeEmail> changeEmailForm = form(ChangeEmail.class).bindFromRequest();
        if (changeEmailForm.hasErrors()) {
            return badRequest(settings.render(form(ChangePass.class),changeEmailForm,user));
        } else {
            user.changeEmail(changeEmailForm.get().email);
            session().clear();
            session("email", changeEmailForm.get().email);
            flash("success", "Email Changed");
            return ok(settings.render(form(ChangePass.class),form(ChangeEmail.class),user));
        }
    }

    public static Result addNeed(){
        User user = User.findByEmail(session().get("email"));
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
                && user.role<4
                && (user.email.equals(need.addedBy.email) || (user.role<3&& (user.charity.id==need.charity.id)))
                )

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
            User admin = User.findByEmail(email);
            user.changeRole(3);
            user.setCharity(admin.charity);
            return ok();
        }
    }

    public static Result listVolunteers(int page){
        String email = session().get("email");
        User user = User.findByEmail(email);
        if(user.role >2){
            return redirect(routes.Application.index());
        }
        PagingList<User> pagingList;
        //admins can remove leaders and volunteers
        if(user.role==1){
            pagingList = User.find.where()
                    .or(Expr.eq("role", 3),Expr.eq("role",2))
                    .findPagingList(10);
        }
        else{
            //leaders can remove volunteers
            //users that have role volunteer and same charity as user
            pagingList = User.find.where()
                    .eq("role",3)
                    .eq("charity",user.charity)
                    .findPagingList(10);
        }
        Page<User> currentPage = pagingList.getPage(page - 1);
        List<User> volunteer = currentPage.getList();

        Integer totalPageCount = pagingList.getTotalPageCount();
        return ok(volunteers.render(
                        user,volunteer, page, totalPageCount)
        );
    }

    public static Result deleteVolunteer(String id){
        String email = session().get("email");
        User user = User.findByEmail(email);
        User volunteer = User.findByEmail(id);
        if((user.role > 2 ||
                user.role==2&&!user.charity.equals(volunteer.charity))){
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
            editNeeds.category = need.category.toString();
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
        if (editNeedForm.hasErrors()) {
            return badRequest(editNeed.render(editNeedForm, id));
        }
        else if(editNeedForm.get().amount<=need.donatedAmount){
            flash("error","Cannot change amount to less than or equal the amount already donated");
            return badRequest(editNeed.render(editNeedForm,id));
        } else {


            if(editNeedForm.get().message.length()>1) {
                Updates update = new Updates(editNeedForm.get().message, need);
            }
            if(editNeedForm.get().closed){
                //close it
                need.markAsClosed();
                //send an update as to the reason
                Updates updates = new Updates(editNeedForm.get().reason, need);
            }
            Need.Category category;
            int urgency;
            if(editNeedForm.get().category == null){
                category = need.category;
            } else {
                category = Need.Category.valueOf(editNeedForm.get().category);
            }
            if(editNeedForm.get().urgency == 0){
                urgency = need.urgency;
            }else {
                urgency = editNeedForm.get().urgency;
            }

            need.editNeed(editNeedForm.get().title, editNeedForm.get().description,
                    editNeedForm.get().location, editNeedForm.get().amount,
                    urgency, category);
            flash("success", "Need has been updated");
            return redirect(routes.Profile.profile(1));
        }
    }

    public static Result subscriptions(String email){
        User user = User.findByEmail(email);
        List<Donation> donations = Donation.find.where()
                .eq("donator", user)
                .findList();
        return ok(subscriptions.render(donations,user));
    }

    public static Result unsubFromNeed(long id, String email){
        Donation.find.byId(id).unsub();
        return subscriptions(email);
    }

    public static Result subToNeed(long id, String email){
        Donation.find.byId(id).sub();
        return redirect(routes.Profile.subscriptions(email));
    }

    public static Result unsubFromAll(String email){
        User user = User.findByEmail(email);
        List<Donation> donations = Donation.find.where()
                .eq("donator", user)
                .findList();
        for(Donation donation:donations) {
            donation.unsub();
        }
        return redirect(routes.Profile.subscriptions(email));
    }

    public static Result editCharity(){
        Form<EditCharity> editCharityForm = form(EditCharity.class).bindFromRequest();
        String email = session().get("email");
        User user = User.findByEmail(email);
        Charity charity = user.charity;
        charity.editCharity(editCharityForm.get().charityName,
                editCharityForm.get().description,editCharityForm.get().website);
        return profile(1);
    }

    public static Form<EditCharity> preFillEditNeedForm(User user){
        if(user.charity!=null){
            Charity charity = user.charity;
            Form<EditCharity> editCharityForm = form(EditCharity.class);
            EditCharity addCharity = new EditCharity();
            addCharity.charityName = charity.name;
            addCharity.description = charity.description;
            addCharity.website = charity.website;
            editCharityForm = editCharityForm.fill(addCharity);
            return editCharityForm;
        }
        else {
            return form(EditCharity.class);
        }

    }

}